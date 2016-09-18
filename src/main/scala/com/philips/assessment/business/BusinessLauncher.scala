package com.philips.assessment.business

import akka.actor.{ActorSystem, Props}
import com.philips.assessment.business.actors.BusinessActorController
import com.typesafe.config.ConfigFactory

/**
  * Created by roberto on 17/09/2016.
  */
class BusinessLauncher {

  def run (actorPort : Int) = {
    val clusterConfiguration =
      ConfigFactory.parseString(s"akka.remote.netty.tcp.port=" + actorPort)
        .withFallback(ConfigFactory.parseString("akka.cluster.roles = [business]"))
        .withFallback(ConfigFactory.load())

    val system = ActorSystem("ClusterSystem", clusterConfiguration)

    system.actorOf(Props(classOf[BusinessActorController]), name = "business")
  }

}
