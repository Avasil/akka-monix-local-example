val monixVersion      = "3.2.2+49-d027e5f7-SNAPSHOT"
val akkaHttpVersion   = "10.1.11"
val akkaStreamVersion = "2.6.3"

resolvers += Resolver.mavenLocal
resolvers in ThisBuild += Resolver.sonatypeRepo("snapshots")

fork := true

libraryDependencies := Seq(
  "com.typesafe.akka"          %% "akka-http"      % akkaHttpVersion,
  "com.typesafe.akka"          %% "akka-stream"    % akkaStreamVersion,
  "io.monix"                   %% "monix"          % monixVersion,
  "ch.qos.logback"             % "logback-classic" % "1.2.3",
  "com.typesafe.scala-logging" %% "scala-logging"  % "3.9.2"
)
