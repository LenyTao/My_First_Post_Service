name := "My_First_Post_Service"

version := "0.1"

scalaVersion := "2.12.10"

val akkaVersion = "2.6.14"

libraryDependencies ++= Seq(
  "org.scalatest"              %% "scalatest"         % "3.2.7" % "test",
  "com.typesafe"               % "config"             % "1.4.1",
  "io.spray"                   %% "spray-json"        % "1.3.6",
  "org.apache.kafka"           % "kafka-clients"      % "2.3.1",
  "com.typesafe.akka"          %% "akka-actor-typed"  % akkaVersion,
  "com.typesafe.akka"          %% "akka-stream-kafka" % "2.0.7",
  "com.typesafe.akka"          %% "akka-stream"       % akkaVersion,
  "com.sun.mail"               % "javax.mail"         % "1.6.0",
  "org.slf4j"                  % "slf4j-simple"       % "1.6.2",
  "com.typesafe.scala-logging" %% "scala-logging"     % "3.9.3"
)
