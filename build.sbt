name := "My_First_Post_Service"

version := "0.1"

scalaVersion := "2.12.10"

val akkaVersion = "2.6.14"

libraryDependencies ++= Seq(
  //Scala Test
  "org.scalatest" %% "scalatest" % "3.2.7" % "test",
  //Config
  "com.typesafe" % "config" % "1.4.1",
  //Json
  "io.spray" %% "spray-json" % "1.3.6",
  //Kafka
  "org.apache.kafka" % "kafka-clients" % "2.3.1",
  //Akka
  "com.typesafe.akka" %% "akka-actor-typed"  % akkaVersion,
  "com.typesafe.akka" %% "akka-stream-kafka" % "2.0.7",
  "com.typesafe.akka" %% "akka-stream"       % akkaVersion,
  //Java Mail
  "com.sun.mail" % "javax.mail" % "1.6.0"
)
