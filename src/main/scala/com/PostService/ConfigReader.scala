package com.PostService

import com.typesafe.config.{Config, ConfigFactory}

import scala.collection.convert.ImplicitConversions.`list asScalaBuffer`

object ConfigReader {
  private case class ClientObject(params: Config) {
    val idClient: String = params.getString("id")
    val nameClient: String = params.getString("name")
    val postClient: String = params.getString("post")
  }

  def findPostByID(clientId: String) = {
    val config = ConfigFactory.load("application.conf")

    val listClientId = config.getConfigList("my.clientList.team.clients") map (ClientObject(_).idClient)
    val listClientPost = config.getConfigList("my.clientList.team.clients") map (ClientObject(_).postClient)

    val listClientIdAndPost = listClientId.zip(listClientPost).toMap

    listClientIdAndPost.filter(x => x._1 == clientId).getOrElse(clientId, "Error")
  }
}
