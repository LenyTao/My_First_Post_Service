package com.example.notificator.notificationservice

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ ActorSystem, Behavior }
import akka.kafka.{ ConsumerSettings, Subscriptions }
import com.example.notificator.Event
import com.typesafe.scalalogging.Logger
import org.apache.kafka.clients.consumer.{ ConsumerConfig, ConsumerRecord }
import org.apache.kafka.common.serialization.{ IntegerDeserializer, StringDeserializer }
import org.slf4j.LoggerFactory
import java.time.Duration
import spray.json._

object NotificationServiceEngine extends App with JsonConverter {
  private val logger = Logger(LoggerFactory.getLogger(this.getClass))

  private val collectionEmailAddressAndIDClients = ConfigReader.readClientList()

  private val messenger: Behavior[Event] = Behaviors.setup { _ =>
    Behaviors.receiveMessage {
      case event: Event =>
        val clientID = event.externalClientId.split("-")(0)
        val clientMail =
          collectionEmailAddressAndIDClients
            .filter(client => client._1 == clientID)
            .getOrElse(clientID, "INVALID ID")

        if (clientMail != "INVALID ID") {
          SenderOfLetters.sendMail(clientMail, event)
        } else {
          logger.error(s"Error: The email address for a customer with ID $clientID does not exist in Database")
        }
        Behaviors.same
      case ex =>
        logger.error(s"Error: Invalid Event - message $ex not valid")
        Behaviors.same
    }
  }

  private val readerFromKafka: Behavior[ConsumerRecord[Integer, String]] = Behaviors.setup { _ =>
    Behaviors.receiveMessage {
      case x: ConsumerRecord[Integer, String] =>
        messengerActor ! converterEvents.read(x.value().parseJson)
        Behaviors.same
      case ex =>
        logger.error(s"Error: Invalid Event - record $ex not valid")
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
      .withBootstrapServers(ConfigReader.readConnectionParameters().bootstrapServers)
      .withGroupId(ConfigReader.readConnectionParameters().groupId)
      .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, ConfigReader.readConnectionParameters().property)
      .withStopTimeout(Duration.ofSeconds(ConfigReader.readConnectionParameters().timeout))

  private val messengerActor = mailServiceEngineActorSystem.systemActorOf(messenger, "MessengerActor")

  akka.kafka.scaladsl.Consumer
    .atMostOnceSource(consumerSettings, Subscriptions.topics("notificationMessageStore"))
    .map { msg =>
      mailServiceEngineActorSystem ! msg
      msg
    }
    .run
}
