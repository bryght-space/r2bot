package com.bryghts.r2bot

import sbt._
import sbt.Keys._
import sbt.plugins.JvmPlugin
import com.bryghts.r2bot.R2GlobalDocs._
import com.bryghts.r2bot.{caps => r2caps}
import com.bryghts.r2bot.caps.sbtplugin.R2SbtpluginKeys
import com.bryghts.r2bot.caps.mavencentral.R2MavencentralKeys

object R2botPlugin extends AutoPlugin with R2ExtensionMethods {

  override def trigger = allRequirements
  override def requires = JvmPlugin

  object autoImport extends R2ExtensionMethods
                       with R2MetaKeys
                       with R2SbtpluginKeys
                       with R2MavencentralKeys
                       with R2GlobalDocsKeys {

    object r2 extends R2GlobalDocsHelpers

    object caps {

      val SbtPlugin =
        r2caps.sbtplugin.SbtpluginCapability

      val MavenCentral =
        r2caps.mavencentral.MavencentralCapability

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
  import R2botPlugin.autoImport._

  implicit class R2ProjectOps(val p: Project) {

    def r2Root: ProjectWithoutCapabilities =
      new ProjectWithoutCapabilities(
        p.settings(R2MetaKeys.defaults)
      )

  }

}

class ProjectWithoutCapabilities private[r2bot] (val p: Project) extends AnyVal {

  import _root_.sbtrelease.ReleasePlugin
  import ReleasePlugin.autoImport._
  import ReleaseTransformations._

  def withCapabilities(caps: Capability*): Project =
    caps
      .foldLeft(ProcessedProject(p, Set()))(_ +_)
      .evaluatedProject
      .settings(
         // Giving this a VERY opinionated default value
         licenses := {
           licenses.?.value match {
             case Some(oldValue) => oldValue
             case None =>
               Seq("MIT" -> url("https://opensource.org/licenses/MIT"))
           }
         }
       )
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
           tagRelease) ++                          // : ReleaseStep
         caps.toList.flatMap(
           _.postCommitReleaseVersionReleaseActions) ++
         Seq[ReleaseStep](
           setNextVersion,                         // : ReleaseStep
           commitNextVersion,                      // : ReleaseStep
           pushChanges                             // : ReleaseStep, also checks that an upstream branch is properly configured
         )

      )
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
