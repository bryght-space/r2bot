resolvers -= Resolver.sbtPluginRepo("releases")
externalResolvers -= Resolver.sbtPluginRepo("releases")

libraryDependencies += "org.scala-sbt" %% "scripted-plugin" % sbtVersion.value

addSbtPlugin("org.xerial.sbt"    % "sbt-sonatype" % "3.9.5")
