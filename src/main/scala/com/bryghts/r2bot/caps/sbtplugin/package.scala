package com.bryghts.r2bot
package caps

import sbt._
import sbt.librarymanagement.Resolver

package object sbtplugin {

  trait R2SbtpluginKeys {

    val r2SbtpluginSelfrefFilename =
      settingKey[String]("Name of the file inside the project folder that will selfreference this sbt plugin")

    val r2SbtpluginSelfrefEnabled =
      settingKey[Boolean]("Whether or not this project should selfreference")
        .withRank(KeyRanks.Invisible)

    val r2SbtpluginSelfrefCommitMessage =
      settingKey[String]("Commit message to be used when upgrading the self-refrence")

    val r2SbtpluginDoGenSelfref =
      taskKey[File]("Create a file in the 'project' folder for this plugin to reference itself")

  }

}


