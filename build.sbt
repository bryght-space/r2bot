import ReleaseTransformations._

val domain = "com.bryghts"
val projectName = "apptemplate"
val group = s"$domain.$projectName"



Global / r2GDocsVariables := Map(
  // "VERSION" -> version.value,
  "NAME" -> (name.in(root)).value,
  "GROUP" -> (organization.in(root)).value,
  "YEAR" -> r2.copyrightYearRange(2020),
  "COPYRIGHT_HOLDER" -> "Marc Esquerra <esquerra@bryghts.com>"
)

lazy val root: Project =
  project
    .in(file("."))
    .r2Root
    .withCapabilities(
      caps.SbtPlugin,
      caps.MavenCentral,
     )
    .enablePlugins(ScriptedPlugin)
    .settings(
        r2SbtpluginSelfrefEnabled := true
      , r2MetaLeadDevEmail := "esquerra@bryghts.com"

      , r2MavencentralSonatypeProfileName := "com.bryghts"

      , r2MetaGithubOwnerId   := "bryght-space"
      , r2MetaGithubProjectId := "r2bot"
      , r2MetaLeadDevId       := "marcesquerra"
      , r2MetaLeadDevName     := "Marc Esquerra"
      , r2MetaLeadDevWebsite  := url("https://github.com/marcesquerra")

      , licenses := Seq("MIT" -> url("https://opensource.org/licenses/MIT"))
     )
    .settings(

       name := """r2bot"""
     , organization := "com.bryghts"

     , initialCommands in console := """import com.bryghts.r2bot._"""

    // set up 'scripted; sbt plugin for testing sbt plugins
     , scriptedLaunchOpts ++=
        Seq("-Xmx1024M", "-Dplugin.version=" + version.value)

     , licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

     , addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.13")
     , addSbtPlugin("org.scalameta" % "sbt-mdoc" % "2.2.14")

     , addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "1.0.0")


     , addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "3.9.5")
     , addSbtPlugin("com.jsuereth" % "sbt-pgp" % "2.1.1")

)


