package com.bryghts.r2bot

import sbt._
import sbt.Keys._
import _root_.mdoc.MdocPlugin
import _root_.mdoc.MdocPlugin.autoImport._
import com.bryghts.r2bot.PathOps._
import _root_.sbtrelease.ReleasePlugin
import ReleasePlugin.autoImport.ReleaseStep

object R2GlobalDocs {

  import R2botPlugin.autoImport._

  lazy val r2GlobalDocs =
    project
      .in(file("target/hidden/globalDocs"))
      .enablePlugins(MdocPlugin)
      .settings(
        skip in publish := true,
        mdocIn :=  (ThisBuild / baseDirectory).value / "global-docs",
        mdocOut := (ThisBuild / baseDirectory).value,
        mdocExtraArguments ++= Seq(
          "--markdown-extensions", "md",
          "--markdown-extensions", "_no_ext_"
        ),

        mdocVariables ++= r2GDocsVariables.value ++ Map(
          "VERSION" -> version.value
        )

      )

  trait R2GlobalDocsKeys {

    // Configuration

    val r2GDocsVariables =
      settingKey[Map[String, String]]("Variables to be used on the global docs")

    // Actions
    val r2GDocsDoGen =
      taskKey[Unit]("Generates the global, not associated to any particular project, including '*._no_ext_' ones")
  }

  trait R2GlobalDocsHelpers {

    def copyrightYearRange(from: Int): String = {
      val initialYear = from.toString
      val currentYear = java.time.Year.now.getValue().toString

      if (initialYear == currentYear) initialYear
      else s"$initialYear-$currentYear"
    }

  }

  lazy val r2DocsGlobalSettings: Seq[Def.Setting[_]]= Seq (
    r2GDocsDoGen := {
      val _ = (mdoc.in(r2GlobalDocs)).toTask("").value
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

  val r2DocsExtraProjects =
    Seq(r2GlobalDocs)

  val r2DocsReleaseSteps = Seq[ReleaseStep](
    ReleasePlugin.autoImport.releaseStepTask(r2GDocsDoGen)
  )

}
