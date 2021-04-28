package com.PostService

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.apache.kafka.common.serialization.{IntegerSerializer, StringSerializer}

import java.util.{Date, Properties}

object Producer extends App with ConverterFromToJson {
  val topicName = "test1"

  val producerProperties = new Properties()
  producerProperties.setProperty(
    ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"
  )
  producerProperties.setProperty(
    ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, classOf[IntegerSerializer].getName
  )
  producerProperties.setProperty(
    ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getName
  )

  val producer = new KafkaProducer[Int, String](producerProperties)

  val person1Stage1 = converterEvents.write(Event("1234-00155", "Received", status = false, new Date()))
  val person2Stage1 = converterEvents.write(Event("0012-00157", "Received", status = false, new Date()))
  val person3Stage1 = converterEvents.write(Event("0125-00263", "Received", status = false, new Date()))

  val person1Stage2 = converterEvents.write(Event("1234-00155", "Processed", status = false, new Date()))
  val person2Stage2 = converterEvents.write(Event("0012-00157", "Processed", status = false, new Date()))
  val person3Stage2 = converterEvents.write(Event("0125-00263", "Processed", status = false, new Date()))

  val person1Stage3 = converterEvents.write(Event("1234-00155", "Approved", status = true, new Date()))
  val person2Stage3 = converterEvents.write(Event("0012-00157", "Approved", status = true, new Date()))
  val person3Stage3 = converterEvents.write(Event("0125-00263", "Approved", status = true, new Date()))


  producer.send(new ProducerRecord[Int, String](topicName, 1, person1Stage1.toString()))
  producer.send(new ProducerRecord[Int, String](topicName, 2, person2Stage1.toString()))
  producer.send(new ProducerRecord[Int, String](topicName, 3, person3Stage1.toString()))

  producer.send(new ProducerRecord[Int, String](topicName, 4, person1Stage2.toString()))
  producer.send(new ProducerRecord[Int, String](topicName, 5, person2Stage2.toString()))
  producer.send(new ProducerRecord[Int, String](topicName, 6, person3Stage2.toString()))

  producer.send(new ProducerRecord[Int, String](topicName, 7, person1Stage3.toString()))
  producer.send(new ProducerRecord[Int, String](topicName, 8, person2Stage3.toString()))
  producer.send(new ProducerRecord[Int, String](topicName, 9, person3Stage3.toString()))

  producer.flush()

}
