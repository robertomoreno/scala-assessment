package com.philips.assessment.endpoint.actors

import akka.actor.Actor
import com.philips.assessment.business.actors.BusinessActorController.DoBusinessStuff
import com.philips.assessment.endpoint.actors.EndpointActorController.DoStuff
import com.philips.assessment.utils.actors.{ActorSelector, RandomClusterSelector}


object EndpointActorController {

  trait EndpointMessage

  case object DoStuff extends EndpointMessage

}

class EndpointActorController extends Actor {
  this: ActorSelector =>

  override def receive: Receive = {
    case DoStuff =>
      println("########################")
      println(selectActorPath)
      println("########################")
      selectActorPath match {
        case Some(actor) => actor forward DoBusinessStuff
        case None => throw new NoSuchElementException("service is not available right now")
      }
  }

}

class NewEndpoint extends EndpointActorController with RandomClusterSelector {
  override def receive: Receive = super.receive orElse clusterReceive
}
