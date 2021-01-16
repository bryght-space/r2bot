package com.bryghts.r2bot

import sbt._
import sbt.Keys._
import sbt.plugins.JvmPlugin

object R2botPlugin extends AutoPlugin with R2ExtensionMethods {

  lazy val r2GlobalDocs =
    project
      .in(file("target/hidden/globalDocs"))
      .r2Docs

  override def trigger = allRequirements
  override def requires = JvmPlugin

  object autoImport extends R2ExtensionMethods {
    val r2GenGlobalDocs = taskKey[Unit]("Generates the global, not associated to any particular project, including '*._no_ext_' ones")
    val r2GlobalDocsVariables = settingKey[Map[String, String]]("")
    val r2 = com.bryghts.r2bot.r2
  }

  override lazy val projectSettings = Seq()
  override lazy val buildSettings = Seq()
  override lazy val globalSettings =
    r2.globalSettings(r2GlobalDocs)
  override lazy val extraProjects: Seq[Project] =
    Seq(r2GlobalDocs)
}

object r2 {

  import R2botPlugin.autoImport._
  import mdoc.MdocPlugin.autoImport.mdoc
  import com.bryghts.r2bot.PathOps._

  def globalSettings(docs: ProjectReference): Seq[Setting[_]] = Seq (
    r2GenGlobalDocs := {
      val _ = (mdoc.in(docs)).toTask("").value
      val docsRoot = path("global-docs")
      val extension = "._no_ext_"
      docsRoot
        .allFilesRecursively
        .filter(_.toString.endsWith(extension))
        .map(docsRoot.relativize)
        .foreach{source =>
          val targetName = source.toString.reverse.drop(extension.length).reverse
          val target = path(targetName)
          target.delete()
          source.renameTo(target)
        }
    }
  )

}

trait R2ExtensionMethods {

  import _root_.mdoc.MdocPlugin
  import _root_.mdoc.MdocPlugin.autoImport._
  import _root_.sbtrelease.ReleasePlugin
  import ReleasePlugin.autoImport._
  import ReleaseTransformations._
  import R2botPlugin.autoImport._

  implicit class R2ProjectOps(val p: Project) {

    def r2Docs(): Project = {
      p.enablePlugins(MdocPlugin)
       .settings(
          skip in publish := true,
          mdocIn :=  (ThisBuild / baseDirectory).value / "global-docs",
          mdocOut := (ThisBuild / baseDirectory).value,
          mdocExtraArguments ++= Seq(
            "--markdown-extensions", "md",
            "--markdown-extensions", "_no_ext_"
          ),

          mdocVariables ++= r2GlobalDocsVariables.value ++ Map(
            "VERSION" -> version.value,
            // "NAME" -> (name.in(root)).value,
            // "GROUP" -> (organization.in(root)).value,
            "YEAR" -> {
              val initialYear = "2020"
              val currentYear = java.time.Year.now.getValue().toString

              if (initialYear == currentYear) initialYear
              else s"$initialYear-$currentYear"
            },
            // "COPYRIGHT_HOLDER" -> "Marc Esquerra <esquerra@bryghts.com>"
          )

        )
    }

    def r2Root: Project =
      p
      .settings(

         releaseProcess := Seq[ReleaseStep](
           checkSnapshotDependencies,              // : ReleaseStep
           inquireVersions,                        // : ReleaseStep
           runClean,                               // : ReleaseStep
           runTest,                                // : ReleaseStep
           setReleaseVersion,                      // : ReleaseStep
           ReleasePlugin.autoImport.releaseStepTask(r2GenGlobalDocs),
           ReleasePlugin.autoImport.releaseStepCommand("git add ."),
           commitReleaseVersion,                   // : ReleaseStep, performs the initial git checks
           tagRelease,                             // : ReleaseStep
           setNextVersion,                         // : ReleaseStep
           commitNextVersion,                      // : ReleaseStep
           pushChanges                             // : ReleaseStep, also checks that an upstream branch is properly configured
         )

       , publishTo := Some(
           if (isSnapshot.value)
             Opts.resolver.sonatypeSnapshots
           else
             Opts.resolver.sonatypeStaging
         )

      )
  }

}
