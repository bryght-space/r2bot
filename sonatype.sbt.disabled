import xerial.sbt.Sonatype._

publishMavenStyle := true

sonatypeProfileName := "com.bryghts"
sonatypeProjectHosting := Some(GitHubHosting(user="bryght-space", repository="r2bot", email="esquerra@bryghts.com"))
developers := List(
  Developer(id="marcesquerra", name="Marc Esquerra", email="esquerra@bryghts.com", url=url("https://github.com/marcesquerra"))
)
licenses := Seq("MIT" -> url("https://opensource.org/licenses/MIT"))

publishTo := sonatypePublishToBundle.value


credentials ++= {
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


credentials ++= {
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


//sonatypeLogLevel := "DEBUG"
