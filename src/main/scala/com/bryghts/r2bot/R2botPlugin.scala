package com.bryghts.r2bot

import sbt._
import sbt.Keys._
import sbt.plugins.JvmPlugin
import com.bryghts.r2bot.R2GlobalDocs._
import _root_.com.bryghts.r2bot.{caps => r2caps}

object R2botPlugin extends AutoPlugin with R2ExtensionMethods {

  override def trigger = allRequirements
  override def requires = JvmPlugin

  object autoImport extends R2ExtensionMethods
                       with R2GlobalDocsKeys {

    object r2 extends R2GlobalDocsHelpers

    object caps {
      val Bintray =
        r2caps.bintray.BintrayCapability
    }

  }

  import autoImport._

  override lazy val projectSettings = Seq()
  override lazy val buildSettings = Seq()
  override lazy val extraProjects =
    r2DocsExtraProjects
  override lazy val globalSettings =
    r2DocsGlobalSettings



}


trait R2ExtensionMethods {

  import _root_.sbtrelease.ReleasePlugin
  import ReleasePlugin.autoImport._
  import ReleaseTransformations._
  import R2botPlugin.autoImport._

  implicit class R2ProjectOps(val p: Project) {

    def r2Root: ProjectWithoutCapabilities =
      new ProjectWithoutCapabilities(
        p
        .settings(

           releaseProcess := Seq[ReleaseStep](
             checkSnapshotDependencies,              // : ReleaseStep
             inquireVersions,                        // : ReleaseStep
             runClean,                               // : ReleaseStep
             runTest,                                // : ReleaseStep
             setReleaseVersion) ++                   // : ReleaseStep
           r2DocsReleaseSteps ++
           Seq[ReleaseStep](
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
      )
  }

}

class ProjectWithoutCapabilities private[r2bot] (val p: Project) extends AnyVal {
  def withCapabilities(caps: Capability*): Project =
    caps
      .foldLeft(ProcessedProject(p, Set(bintray.BintrayPlugin)))(_ +_)
      .evaluatedProject
}

case class ProcessedProject(
  p: Project,
  excludedPlugins: Set[AutoPlugin],
  includedPlugins: Set[Plugins] = Set.empty
) {

  def + (cap: Capability): ProcessedProject =
    ProcessedProject (
      cap.applyConfiguration(p),
      excludedPlugins -- cap.enabledAutoPlugins,
      includedPlugins ++ cap.enabledPlugins )

  def evaluatedProject: Project =
    p
      .enablePlugins(includedPlugins.toSeq  :_*)
      .disablePlugins(excludedPlugins.toSeq :_*)

}
