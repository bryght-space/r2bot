package com.bryghts.r2bot

import sbt._
import sbt.Keys._
import com.bryghts.r2bot.R2botPlugin.autoImport._

trait R2MetaKeys {

  val r2MetaOwnerId = settingKey[String]("Id commonly used to identify the owner (user/organization/team) of the project")

}

object R2MetaKeys {

  val defaults: Seq[Def.Setting[_]] = Seq(
    r2MetaOwnerId := organization.value
  )

}
