package com.bryghts.r2bot
package caps

import sbt._
import sbt.librarymanagement.Resolver

package object sbtplugin {

  trait R2SbtpluginKeys {

    val r2SbtpluginSelfrefFilename =
      settingKey[String]("Name of the file inside the project folder that will selfreference this sbt plugin")

    val r2SbtpluginDoGenSelfref =
      settingKey[File]("Create a file in the 'project' folder for this plugin to reference itself")

  }

}


