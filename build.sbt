import Dependencies._

name := "My_First_Post_Service"

version := "0.1"

scalaVersion := s"$scalaCompat"

libraryDependencies ++= Seq(
  Logging.Slf4j,
  Logging.ScalaLogging,
  Config.Config,
  JsonConverter.Converter,
  Akka.Stream,
  Akka.AkkaTyped,
  Akka.ConnectorAkkaKafka,
  Kafka.Kafka,
  JavaMail.JavaMail,
  Testing.ScalaTest
)
