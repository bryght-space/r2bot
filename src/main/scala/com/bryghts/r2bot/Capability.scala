package com.bryghts.r2bot

import sbt._

trait Capability {

  def applyConfiguration(p: Project): Project

}
