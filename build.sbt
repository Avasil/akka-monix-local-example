val monixVersion      = "3.3.0"
val akkaHttpVersion   = "10.1.11"
val akkaStreamVersion = "2.6.3"
val monixMdcVersion   = "0.1.8"

scalaVersion := "2.13.3"
resolvers += Resolver.mavenLocal
resolvers in ThisBuild += Resolver.sonatypeRepo("snapshots")

fork := true

libraryDependencies := Seq(
  "com.typesafe.akka"          %% "akka-http"         % akkaHttpVersion,
  "com.typesafe.akka"          %% "akka-stream"       % akkaStreamVersion,
  "io.monix"                   %% "monix-eval"        % monixVersion,
  "io.monix"                   %% "monix-mdc-logback" % monixMdcVersion,
  "ch.qos.logback"             % "logback-classic"    % "1.2.3",
  "com.typesafe.scala-logging" %% "scala-logging"     % "3.9.2"
)
