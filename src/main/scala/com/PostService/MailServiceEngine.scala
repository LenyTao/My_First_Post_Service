package com.PostService

import akka.Done
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorSystem, Behavior}
import akka.kafka.{ConsumerSettings, Subscriptions}
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.kafka.common.serialization.{IntegerDeserializer, StringDeserializer}

import java.time.Duration
import scala.concurrent.Future
import spray.json._


object MailServiceEngine extends App with ConverterFromToJson {
  val messenger: Behavior[Event] = Behaviors.setup { _ =>
    Behaviors.receiveMessage {
      case event: Event =>

        /** Вытаскивает ID пользователя по нему ищет почту */
        val clientMail = ConfigReader.findPostByID(event.externalClientId.split("-")(0))
        println(event)

        /** Отправляет сообщение */
        SenderOfLetters.sendMail(clientMail, event)
        Behaviors.same
      case _ => println("Error")
        Behaviors.same
    }
  }

  val readerFromKafka: Behavior[Any] = Behaviors.setup { _ =>
    Behaviors.receiveMessage {
      case x: ConsumerRecord[Integer, String] =>
        messengerActor ! converterEvents.read(x.value().parseJson)
        Behaviors.same
      case _ => println("Error")
        Behaviors.same
    }
  }

  val postServiceActorSystem = ActorSystem(
    readerFromKafka, "PostServiceActorSystem"
  )

  implicit val actorSystem: akka.actor.ActorSystem = akka.actor.ActorSystem("ConnectorSystem")
  val consumerSettings: ConsumerSettings[Integer, String] =
    ConsumerSettings
      .create(actorSystem, new IntegerDeserializer(), new StringDeserializer())
      .withBootstrapServers("localhost:9092")
      .withGroupId("group-id-2")
      .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
      .withStopTimeout(Duration.ofSeconds(5))

  val messengerActor = postServiceActorSystem.systemActorOf(messenger, "Messenger")

  val pipeline: Future[Done] =
    akka.kafka.scaladsl.Consumer
      .atMostOnceSource(consumerSettings, Subscriptions.topics("test1"))
      .map { msg =>
        postServiceActorSystem ! msg
        msg
      }
      .run
}
