package com.example.notificator.notificationservice

import com.typesafe.config.{ Config, ConfigFactory }
import scala.collection.convert.ImplicitConversions.`list asScalaBuffer`

object ConfigReader {

  case class ConnectionParameters(bootstrapServers: String, groupId: String, property: String, timeout: Int)

  private case class ClientObject(params: Config) {
    val idClient: String   = params.getString("id")
    val postClient: String = params.getString("post")
  }

  def readConnectionParameters() = {
    val config = ConfigFactory.load("application.conf").getConfig("my.clientList.сonnectionParameters");

    val bootstrapServers: String = config.getString("bootstrapServers")
    val groupId: String          = config.getString("groupId")
    val property: String         = config.getString("property")
    val timeout: Int             = config.getInt("timeout")
    ConnectionParameters(bootstrapServers, groupId, property, timeout)
  }

  def readClientList() = {
    val config: Config = ConfigFactory.load("application.conf")
    val listClientId   = config.getConfigList("my.clientList.team.clients") map (ClientObject(_).idClient)
    val listClientPost = config.getConfigList("my.clientList.team.clients") map (ClientObject(_).postClient)
    listClientId.zip(listClientPost).toMap
  }
}
