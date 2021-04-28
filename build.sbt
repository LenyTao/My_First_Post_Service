name := "My_First_Post_Service"

version := "0.1"

scalaVersion := "2.12.10"

val AkkaVersion = "2.6.9"
val AkkaHttpVersion = "10.1.12"
val AlpakkaKafkaVersion = "2.0.5"

libraryDependencies ++= Seq(
  //Akka
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion,
  "com.typesafe.akka" %% "akka-stream-kafka" % AlpakkaKafkaVersion,
  //  "org.slf4j" % "slf4j-simple" % "1.7.30",
  "com.typesafe.akka" %% "akka-stream-typed" % AkkaVersion,
  //Jva Mail
  "javax.mail" % "javax.mail-api" % "1.6.2",
  "com.sun.mail" % "javax.mail" % "1.6.2",
  //Config
  "com.typesafe" % "config" % "1.4.1",
  //Scala Test
  "org.scalatest" %% "scalatest" % "3.2.7" % "test"
)



