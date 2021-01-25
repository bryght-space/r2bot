package com.bryghts.r2bot
package caps.bintray

import sbt._
import bintray.BintrayPlugin
import bintray.BintrayKeys._
import com.bryghts.r2bot.R2botPlugin.autoImport._

object BintrayCapability extends Capability {

  override def applyConfiguration(p: Project): Project = {

    p.settings(
      // Capability settings
      r2BintrayOwnedByOrganization := false,
      r2BintrayOwner := {
        if(r2BintrayOwnedByOrganization.value)
          R2BintrayOwner.R2BintrayOrganization(r2MetaOwnerId.value)
        else
          R2BintrayOwner.R2BintrayUser
      },
      r2BintrayRepository := "maven",
      r2BintrayPublishTo := (Keys.publishTo in _root_.bintray.BintrayPlugin.autoImport.bintray).value,

      // Target settings
      bintrayOrganization := r2BintrayOwner.value.asSetting,
      bintrayRepository := r2BintrayRepository.value,

      // Commands
      addCommandAlias(
        "r2BintrayDoPublish",
        ";set publishTo := r2BintrayPublishTo.value;publish")
    )

  }

  override def enabledPlugins: Set[Plugins] =
    Set(BintrayPlugin)


}
