package com.bryghts.r2bot
package caps

import sbt._
import sbt.librarymanagement.Resolver

package object mavencentral {

  trait R2MavencentralKeys {

    val r2MavencentralGpgPrivateKey =
      taskKey[Credentials]("Credentials configuring the GPG private key")

    val r2MavencentralSonatypeCredentials =
      taskKey[Credentials]("Sonatype credentials")

    val r2MavencentralSonatypeProfileName =
      settingKey[String]("Sonatype profile name")

  }

}


