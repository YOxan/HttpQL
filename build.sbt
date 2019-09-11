name := "graphql-backend"

scalaVersion := "2.12.8"

val http4sVersion         = "0.20.6"
val logbackVersion        = "1.2.3"
val circeVersion          = "0.11.1"
val tapirVersion          = "0.9.0"
val catsCoreVersion       = "1.6.1"
val catsEffectVersion     = "1.3.1"
val sangriaVersion        = "1.4.2"
val sangriaCirceVersion   = "1.2.1"
val typesafeConfigVersion = "1.3.4"
val peruconfigVersion     = "0.11.1"
val jwtCirceVersion       = "3.1.0"

// Only necessary for SNAPSHOT releases
resolvers += Resolver.sonatypeRepo("snapshots")
resolvers += Resolver.sonatypeRepo("releases")
resolvers += Resolver.bintrayRepo("janstenpickle", "maven")

addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.10.3")
enablePlugins(GitVersioning, GitBranchPrompt)

scalacOptions ++= Seq(
  "-encoding",
  "utf8",
  "-Xfatal-warnings",
  "-deprecation",
  "-unchecked",
  "-language:implicitConversions",
  "-language:higherKinds",
  "-language:existentials",
  "-language:postfixOps"
)

libraryDependencies ++= Seq(
  "org.http4s"             %% "http4s-dsl"               % http4sVersion,
  "org.http4s"             %% "http4s-blaze-server"      % http4sVersion,
  "org.http4s"             %% "http4s-blaze-client"      % http4sVersion,
  "org.typelevel"          %% "cats-core"                % catsCoreVersion,
  "org.typelevel"          %% "cats-effect"              % catsEffectVersion,
  "ch.qos.logback"         % "logback-classic"           % logbackVersion,
  "ch.qos.logback"         % "logback-core"              % logbackVersion,
  "com.softwaremill.tapir" %% "tapir-core"               % tapirVersion,
  "com.softwaremill.tapir" %% "tapir-json-circe"         % tapirVersion,
  "com.softwaremill.tapir" %% "tapir-http4s-server"      % tapirVersion,
  "com.softwaremill.tapir" %% "tapir-openapi-docs"       % tapirVersion,
  "com.softwaremill.tapir" %% "tapir-openapi-circe-yaml" % tapirVersion,
  "com.softwaremill.tapir" %% "tapir-swagger-ui-http4s"  % tapirVersion,
  "io.circe"               %% "circe-core"               % circeVersion,
  "io.circe"               %% "circe-generic"            % circeVersion,
  "io.circe"               %% "circe-parser"             % circeVersion,
  "org.sangria-graphql"    %% "sangria"                  % sangriaVersion,
  "org.sangria-graphql"    %% "sangria-circe"            % sangriaCirceVersion,
  "com.typesafe"           % "config"                    % typesafeConfigVersion,
  "com.github.pureconfig"  %% "pureconfig"               % peruconfigVersion,
  "com.pauldijou"          %% "jwt-circe"                % jwtCirceVersion excludeAll "io.circe"
)

val VersionRegex = "v([0-9]+.[0-9]+.[0-9]+)-?(.*)?".r
git.useGitDescribe := true
git.baseVersion := "0.0.1"
//TODO: Fix version if there is no tag
git.gitTagToVersionNumber := {
  case VersionRegex(v, "")         => Some(v)
  case VersionRegex(v, "SNAPSHOT") => Some(s"$v-SNAPSHOT")
  case VersionRegex(v, s)          => Some(s"$v-CH-SNAPSHOT")
  case _                           => Some("None")
}
git.formattedShaVersion := git.gitHeadCommit.value map { sha =>
  "0.0.1-CH-SNAPSHOT"
}
