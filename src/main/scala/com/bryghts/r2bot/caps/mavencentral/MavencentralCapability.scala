package com.bryghts.r2bot
package caps.sbtplugin

import sbt._
import sbt.Keys._
import sbt.plugins.SbtPlugin
import com.bryghts.r2bot.R2botPlugin.autoImport._
import sbtrelease.ReleasePlugin.autoImport.ReleaseStep
import sbtrelease.ReleasePlugin

object MavencentralCapability extends Capability {

  override def applyConfiguration(p: Project): Project = {

    p
    .settings(
         publishMavenStyle := true
       , credentials ++= {
           import scala.util.Properties.envOrNone

           envOrNone("MAVEN_CENTRAL_GPG_PRIVATE_KEY_ID") match {
             case Some(key) =>
               Seq(Credentials(
                 "GnuPG Key ID",
                 "gpg",
                 key, // key identifier
                 "ignored" // this field is ignored; passwords are supplied by pinentry
               ))

             case _ =>
               Seq()
           }

         }
       , credentials ++= {
           import scala.util.Properties.envOrNone

           val u = envOrNone("SONATYPE_USERNAME")
           val p = envOrNone("SONATYPE_PASSWORD")

           (u, p) match {
             case (Some(user), Some(pass)) =>
               Seq(Credentials("Sonatype Nexus Repository Manager",
                     "oss.sonatype.org",
                     user,
                     pass))
             case _ =>
               Seq()
           }
         }
    )

  }

}
