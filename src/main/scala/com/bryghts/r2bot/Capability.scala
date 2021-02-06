package com.bryghts.r2bot

import sbt._
import sbtrelease.ReleasePlugin.autoImport.ReleaseStep

trait Capability {

  def applyConfiguration(p: Project): Project

  def enabledPlugins: Set[Plugins] = Set.empty

  def postCommitReleaseVersionReleaseActions: List[ReleaseStep] = Nil

  final def enabledAutoPlugins: Set[AutoPlugin] =
    enabledPlugins
      .collect {
          case ap: AutoPlugin => ap
       }

}
