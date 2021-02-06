package com.bryghts.r2bot
package caps.sbtplugin

import sbt._
import sbt.Keys._
import sbt.plugins.SbtPlugin
import com.bryghts.r2bot.R2botPlugin.autoImport._

object SbtpluginCapability extends Capability {

  override def applyConfiguration(p: Project): Project = {

    p
    .enablePlugins(SbtPlugin)
    .settings(
         sbtPlugin := true
       , r2SbtpluginDoGenSelfref := {
           val base = baseDirectory.value / "project"
           val file = base / "self.sbt"
           val contents =
             s"""|addSbtPlugin("${organization.value}" % "${name.value}" % "${version.value}")
                 |""".stripMargin

           IO.write(file, contents)

           file
         }
    )

  }


}
