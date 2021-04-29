package com.example.notificator.notificationservice

import akka.Done
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ ActorSystem, Behavior }
import akka.kafka.{ ConsumerSettings, Subscriptions }
import com.example.notificator.Event
import org.apache.kafka.clients.consumer.{ ConsumerConfig, ConsumerRecord }
import org.apache.kafka.common.serialization.{ IntegerDeserializer, StringDeserializer }
import java.time.Duration
import scala.concurrent.Future
import spray.json._

object NotificationServiceEngine extends App with ConverterFromToJson {

  private val messenger: Behavior[Event] = Behaviors.setup { _ =>
    Behaviors.receiveMessage {
      case event: Event =>
        val clientMail = ConfigReader.findPostByID(event.externalClientId.split("-")(0))
        SenderOfLetters.sendMail(clientMail, event)
        Behaviors.same
      case _ =>
        println("Error: Invalid Event")
        Behaviors.same
    }
  }

  private val readerFromKafka: Behavior[ConsumerRecord[Integer, String]] = Behaviors.setup { _ =>
    Behaviors.receiveMessage {
      case x: ConsumerRecord[Integer, String] =>
        messengerActor ! converterEvents.read(x.value().parseJson)
        Behaviors.same
      case _ =>
        println("Error: Invalid Record")
        Behaviors.same
    }
  }

  private val mailServiceEngineActorSystem = ActorSystem(
    readerFromKafka,
    "MailServiceEngineActorSystem"
  )

  private implicit val actorSystemForConnectedKafka: akka.actor.ActorSystem =
    akka.actor.ActorSystem("ActorSystemForConnectedKafka")

  private val consumerSettings: ConsumerSettings[Integer, String] =
    ConsumerSettings
      .create(actorSystemForConnectedKafka, new IntegerDeserializer(), new StringDeserializer())
      .withBootstrapServers("localhost:9092")
      .withGroupId("group-id-2")
      .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
      .withStopTimeout(Duration.ofSeconds(5))

  private val messengerActor = mailServiceEngineActorSystem.systemActorOf(messenger, "MessengerActor")

  private val pipeline: Future[Done] =
    akka.kafka.scaladsl.Consumer
      .atMostOnceSource(consumerSettings, Subscriptions.topics("notificationMessageStore"))
      .map { msg =>
        mailServiceEngineActorSystem ! msg
        msg
      }
      .run
}
