package com.bryghts.r2bot
package caps.bintray

import sbt._
import bintray.BintrayPlugin

object BintrayCapability extends Capability {

  override def applyConfiguration(p: Project): Project = {
    p
  }

  override def enabledPlugins: Set[Plugins] =
    Set(BintrayPlugin)


}
