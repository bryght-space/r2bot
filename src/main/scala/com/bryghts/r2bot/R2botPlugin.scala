package com.bryghts.r2bot

import sbt._
import sbt.Keys._
import sbt.plugins.JvmPlugin

object R2botPlugin extends AutoPlugin {

  override def trigger = allRequirements
  override def requires = JvmPlugin

  object autoImport extends R2ExtensionMethods {
    val r2GenDocs = taskKey[Unit]("Generates docs, including '*._no_ext_' ones")
    val r2 = com.bryghts.r2bot.r2
  }

  import autoImport._

  override lazy val projectSettings = Seq(
  )

  override lazy val buildSettings = Seq()

  override lazy val globalSettings = Seq()
}

object r2 {

  import R2botPlugin.autoImport._
  import mdoc.MdocPlugin.autoImport.mdoc
  import com.bryghts.r2bot.PathOps._

  def globalSettings(docs: ProjectReference): Seq[Setting[_]] = Seq (
    r2GenDocs := {
      val _ = (mdoc.in(docs)).toTask("").value
      val docsRoot = path("docs")
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

    def r2Docs(root: => Project, dependOnRoot: Boolean = true): Project = {
      lazy val r : ClasspathDep[ProjectReference] = root
      p.in(file("documentation"))
       .enablePlugins(MdocPlugin)
       .dependsOn((if (dependOnRoot) List(r) else List.empty) :_*)
       .settings(
         if (dependOnRoot) Seq(scalaVersion := (scalaVersion.in(root)).value) else Seq()
        )
       .settings(
          skip in publish := true,
          mdocOut := (ThisBuild / baseDirectory).value,
          mdocExtraArguments ++= Seq(
            "--markdown-extensions", "md",
            "--markdown-extensions", "_no_ext_"
          ),

          mdocVariables ++= Map(
            "VERSION" -> version.value,
            "NAME" -> (name.in(root)).value,
            "GROUP" -> (organization.in(root)).value,
            "YEAR" -> {
              val initialYear = "2020"
              val currentYear = java.time.Year.now.getValue().toString

              if (initialYear == currentYear) initialYear
              else s"$initialYear-$currentYear"
            },
            "COPYRIGHT_HOLDER" -> "Marc Esquerra <esquerra@bryghts.com>"
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
           ReleasePlugin.autoImport.releaseStepTask(r2GenDocs),
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
