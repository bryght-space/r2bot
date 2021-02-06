// resolvers += Resolver.sonatypeRepo("releases")
resolvers += Resolver.sbtPluginRepo("releases")
externalResolvers += Resolver.sbtPluginRepo("releases")

libraryDependencies += "org.scala-sbt" %% "scripted-plugin" % sbtVersion.value

addSbtPlugin("com.bryghts" % "r2bot" % "0.0.16")
addSbtPlugin("org.xerial.sbt"    % "sbt-sonatype" % "3.9.5")
