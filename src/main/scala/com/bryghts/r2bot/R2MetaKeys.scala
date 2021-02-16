package com.bryghts.r2bot

import sbt._
import sbt.Keys._
import com.bryghts.r2bot.R2botPlugin.autoImport._

trait R2MetaKeys {

  val r2MetaOwnerId         = settingKey[String]("Id commonly used to identify the owner (user/organization/team) of the project")
  val r2MetaProjectId       = settingKey[String]("Id commonly used to identify this project")
  val r2MetaGithubOwnerId   = settingKey[String]("Name of the github account owning this project")
  val r2MetaGithubProjectId = settingKey[String]("Name of the github repo holding this project")
  val r2MetaLeadDevId       = settingKey[String]("Id used by the lead dev of the project")
  val r2MetaLeadDevName     = settingKey[String]("Name of the lead def of the project")
  val r2MetaLeadDevEmail    = settingKey[String]("Email of the lead def of the project")
  val r2MetaLeadDevWebsite  = settingKey[URL]("URL of the website of the lead def of the project")
  val r2MetaProjectEmail    = settingKey[Option[String]]("Main contact email of the project")

}

object R2MetaKeys {

  val defaults: Seq[Def.Setting[_]] = Seq(
      r2MetaOwnerId         := organization.value
    , r2MetaProjectId       := name.value
    , r2MetaGithubOwnerId   := r2MetaOwnerId.value
    , r2MetaGithubProjectId := r2MetaProjectId.value
    , r2MetaLeadDevId       := r2MetaOwnerId.value
    , r2MetaLeadDevName     := r2MetaOwnerId.value
    , r2MetaProjectEmail    := r2MetaLeadDevEmail.?.value
    , r2MetaLeadDevWebsite  := url(s"https://github.com/${r2MetaGithubOwnerId.value}")
    , developers :=
        List(
          Developer(
            id    = r2MetaLeadDevEmail.value,
            name  = r2MetaLeadDevName.value,
            email = r2MetaLeadDevEmail.value,
            url   = r2MetaLeadDevWebsite.value)
        )

  )

}
