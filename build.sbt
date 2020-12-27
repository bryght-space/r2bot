
name := """r2bot"""
organization := "com.bryghts"
version := "0.0.1-SNAPSHOT"

sbtPlugin := true

// choose a test framework

// utest
//libraryDependencies += "com.lihaoyi" %% "utest" % "0.4.8" % "test"
//testFrameworks += new TestFramework("utest.runner.Framework")

// ScalaTest
//libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.1" % "test"
//libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"

// Specs2
//libraryDependencies ++= Seq("org.specs2" %% "specs2-core" % "3.9.1" % "test")
//scalacOptions in Test ++= Seq("-Yrangepos")

bintrayPackageLabels := Seq("sbt","plugin")
bintrayVcsUrl := Some("""git@github.com:bryght.space/r2bot.git""")

initialCommands in console := """import com.bryghts.r2bot._"""

enablePlugins(ScriptedPlugin)
// set up 'scripted; sbt plugin for testing sbt plugins
scriptedLaunchOpts ++=
  Seq("-Xmx1024M", "-Dplugin.version=" + version.value)

licenses += ("MIT", url("http://opensource.org/licenses/MIT"))
bintrayOrganization := Some("bryghts")
bintrayRepository := "bryght.space"
