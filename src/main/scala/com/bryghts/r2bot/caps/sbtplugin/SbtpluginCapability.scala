package com.bryghts.r2bot
package caps.sbtplugin

import sbt._
import sbt.Keys._
import sbt.plugins.SbtPlugin
import com.bryghts.r2bot.R2botPlugin.autoImport._
import sbtrelease.ReleasePlugin.autoImport.ReleaseStep

object SbtpluginCapability extends Capability {

  def r2SbtpluginDoGenAndReleaseSelfref = Command.command("r2SbtpluginDoGenAndReleaseSelfref") { state =>
    val extracted: Extracted = Project.extract(state)
    import extracted._
    
    val enabled = (currentRef / r2SbtpluginSelfrefEnabled).get(structure.data).getOrElse(false)

    if(enabled) {

      val msg =
        (currentRef / r2SbtpluginSelfrefCommitMessage)
          .get(structure.data)
          .getOrElse("Upgrading self-refrence")

      Project.runTask((Compile / r2SbtpluginDoGenSelfref).scopedKey, state)
      Command.process("git add .", state)
      Command.process(s"git commit -m $msg", state)
    }
    else
      println("Skiping generation of sbtplugin Selfref")

    state
  }

  override def applyConfiguration(p: Project): Project = {

    p
    .enablePlugins(SbtPlugin)
    .settings(
         sbtPlugin := true
       , r2SbtpluginSelfrefEnabled := false
       , r2SbtpluginSelfrefFilename := "self.sbt"
       , r2SbtpluginDoGenSelfref := {
           val base = baseDirectory.value / "project"
           val fileName = r2SbtpluginSelfrefFilename.value
           val file = base / fileName
           val contents =
             s"""|addSbtPlugin("${organization.value}" % "${name.value}" % "${version.value}")
                 |""".stripMargin

           IO.write(file, contents)

           file
         }
       , r2SbtpluginSelfrefCommitMessage := {
           val v = version.value
           s"Upgrading self-refrence to version '$v'"
         }
       , commands += r2SbtpluginDoGenAndReleaseSelfref
    )

  }

  override def postCommitReleaseVersionReleaseActions: List[ReleaseStep] =
    ReleasePlugin.autoImport.releaseStepCommand("r2SbtpluginDoGenAndReleaseSelfref") ::
    Nil

}
