package com.bryghts.r2bot

import sbt._

trait Capability {

  def applyConfiguration(p: Project): Project

  def enabledPlugins: Set[Plugins] = Set.empty

  final def enabledAutoPlugins: Set[AutoPlugin] =
    enabledPlugins
      .collect {
          case ap: AutoPlugin => ap
       }

}
