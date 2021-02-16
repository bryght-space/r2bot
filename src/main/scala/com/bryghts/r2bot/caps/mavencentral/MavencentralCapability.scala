package com.bryghts.r2bot
package caps.mavencentral

import sbt._
import sbt.Keys._
import sbt.plugins.SbtPlugin
import com.bryghts.r2bot.R2botPlugin.autoImport._
import sbtrelease.ReleasePlugin.autoImport.ReleaseStep
import sbtrelease.ReleasePlugin
import xerial.sbt.Sonatype._
import xerial.sbt.Sonatype.SonatypeKeys._

object MavencentralCapability extends Capability {

  import scala.util.Properties.envOrNone

  private def envOrError(env: String, err: String): Either[String, String] =
    envOrNone(env).map(Right.apply).getOrElse(Left(err))
  private def envOrKeyNotFound(env: String): Either[String, String] =
    envOrError(env, s"'$env' not set")

  override def applyConfiguration(p: Project): Project = {

    p
    .settings(
         r2MavencentralGpgPrivateKey := {

           envOrKeyNotFound("MAVEN_CENTRAL_GPG_PRIVATE_KEY_ID") match {
             case Right(key) =>
               Credentials(
                 "GnuPG Key ID",
                 "gpg",
                 key, // key identifier
                 "ignored" // this field is ignored; passwords are supplied by pinentry
               )

             case Left(error) =>
               val log = sLog.value
               log.error(error)
               log.reportFailure(
                "The ID of the key to sign the artifact (for publishing to maven central) could not be loaded")
           }

         }
       , sonatypeProjectHosting :=
          r2MetaProjectEmail.value.map{email =>
            GitHubHosting(user=r2MetaGithubOwnerId.value, repository=r2MetaGithubProjectId.value, email=email)
          }
       , r2MavencentralSonatypeProfileName := r2MetaOwnerId.value
       , r2MavencentralSonatypeCredentials := {
           import sbt.Keys.streams

           val u = envOrKeyNotFound("SONATYPE_USERNAME")
           val p = envOrKeyNotFound("SONATYPE_PASSWORD")

           List(u, p) match {
             case List(Right(user), Right(pass)) =>
               Credentials("Sonatype Nexus Repository Manager",
                     "oss.sonatype.org",
                     user,
                     pass)
             case l =>
               val log = sLog.value
               val errors = l.collect{case Left(e) => e}
               errors.foreach(err => log.error(err))
               log.reportFailure("Sonatype credentials could not be loaded")
           }
         }

      // Commands
      , addCommandAlias(
          "r2MavencentralDoPublish",
          List(
              "set publishTo := sonatypePublishToBundle.value"
            , "set publishMavenStyle := true"
            , "set credentials := Seq(r2MavencentralGpgPrivateKey.value, r2MavencentralSonatypeCredentials.value)"
            , "set sonatypeProfileName := r2MavencentralSonatypeProfileName.value"
            , "publishSigned"
            , "sonatypeBundleRelease"
          ).mkString(";", ";", "")
        )
    )

  }

}
