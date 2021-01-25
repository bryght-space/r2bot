package com.bryghts.r2bot
package caps

import sbt._
import sbt.librarymanagement.Resolver

package object bintray {

  sealed trait R2BintrayOwner {
    def asSetting: Option[String] =
      this match {
        case R2BintrayOwner.R2BintrayUser => None
        case R2BintrayOwner.R2BintrayOrganization(id) => Some(id)
      }
  }

  object R2BintrayOwner {
    case object R2BintrayUser                     extends R2BintrayOwner
    case class  R2BintrayOrganization(id: String) extends R2BintrayOwner
  }

  trait R2BintrayKeys {
    val r2BintrayOwner =
      settingKey[R2BintrayOwner]("Which repository to deploy to, the owner one, or one of it's organizations")

    val r2BintrayOwnedByOrganization =
      settingKey[Boolean]("Wheather this project is owned by an organization, or directly by a user")

    val r2BintrayRepository =
      settingKey[String]("Id of the bintray repository where this project is to be deployed to")

    val r2BintrayPublishTo =
      taskKey[Option[Resolver]]("Configures where to publish to, when publishing to bintray")
  }

}


