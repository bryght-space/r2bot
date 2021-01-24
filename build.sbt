import ReleaseTransformations._

val domain = "com.bryghts"
val projectName = "apptemplate"
val group = s"$domain.$projectName"

organization in ThisBuild := group

lazy val root: Project =
  project
    .in(file("."))
    .enablePlugins(SbtPlugin)
    .enablePlugins(ScriptedPlugin)
    .settings(

   name := """r2bot"""
 , organization := "com.bryghts"

 , sbtPlugin := true

 , bintrayPackageLabels := Seq("sbt","plugin")
 , bintrayVcsUrl := Some("""git@github.com:bryght-space/r2bot.git""")

 , initialCommands in console := """import com.bryghts.r2bot._"""

// set up 'scripted; sbt plugin for testing sbt plugins
 , scriptedLaunchOpts ++=
    Seq("-Xmx1024M", "-Dplugin.version=" + version.value)

 , licenses += ("MIT", url("http://opensource.org/licenses/MIT"))
 , bintrayOrganization := Some("bryghts")
 , bintrayRepository := "bryght.space"


 , releaseProcess := Seq[ReleaseStep](
     checkSnapshotDependencies,              // : ReleaseStep
     inquireVersions,                        // : ReleaseStep
     runClean,                               // : ReleaseStep
     runTest,                                // : ReleaseStep
     setReleaseVersion,                      // : ReleaseStep
     ReleasePlugin.autoImport.releaseStepTask(genDocs),
     ReleasePlugin.autoImport.releaseStepCommand("git add ."),
     commitReleaseVersion,                   // : ReleaseStep, performs the initial git checks
     tagRelease,                             // : ReleaseStep
     setNextVersion,                         // : ReleaseStep
     commitNextVersion,                      // : ReleaseStep
     pushChanges                             // : ReleaseStep, also checks that an upstream branch is properly configured
   )

 , addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.13")
 , addSbtPlugin("org.scalameta" % "sbt-mdoc" % "2.2.14")

 , addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "1.0.0")


 , addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "3.9.5")
 , addSbtPlugin("com.jsuereth" % "sbt-pgp" % "2.1.1")

 , addSbtPlugin("org.foundweekends" % "sbt-bintray" % "0.6.1")


)

import java.nio.file.Path
def files(root: Path): Iterator[Path] = {
  import java.nio.file.Files
  import scala.collection.JavaConverters._
  Files.walk(root).iterator().asScala.filter(Files.isRegularFile(_))
}

val genDocs = taskKey[Unit]("Generates docs, including '*._no_ext_' ones")

genDocs := {
  val _ = (mdoc.in(docs)).toTask("").value
  val docsRoot = file("docs").toPath
  val extension = "._no_ext_"
  files(docsRoot)
    .filter(_.toString.endsWith(extension))
    .map(docsRoot.relativize)
    .foreach{source =>
      val targetName = source.toString.reverse.drop(extension.length).reverse
      val target = file(targetName)
      target.delete()
      source.toFile.renameTo(target)
    }
}


lazy val docs =
  project
    .in(file("documentation"))
    // .dependsOn(root)
    .settings(
       skip in publish := true,
       mdocOut := (ThisBuild / baseDirectory).value,
       mdocExtraArguments ++= Seq(
         "--markdown-extensions", "md",
         "--markdown-extensions", "_no_ext_"
       ),
       mdocVariables := Map(
         "VERSION" -> version.value,
         "NAME" -> (name.in(root)).value,
         "GROUP" -> (organization.in(root)).value,
         "YEAR" -> {
           val initialYear = "2020"
           val currentYear = java.time.Year.now.getValue().toString

           if (initialYear == currentYear) initialYear
           else s"$initialYear-$currentYear"
         },
         "COPYRIGHT_HOLDER" -> "Marc Esquerra <esquerra@bryghts.com>"
       )
    )
    .enablePlugins(MdocPlugin)
