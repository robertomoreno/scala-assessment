package com.philips.assessment.endpoint.actors

import akka.actor.{Actor, ActorSelection}
import com.philips.assessment.business.actors.BusinessActorController.DoBusinessStuff
import com.philips.assessment.endpoint.actors.EndpointActorController.DoStuff
import com.philips.assessment.utils.ClusterSupport

import scala.util.Random


object EndpointActorController {
  trait EndpointMessage
  case object DoStuff extends EndpointMessage
}

class EndpointActorController() extends Actor with ClusterSupport{

  override def receive: Receive = endpointReceive orElse clusterReceive

  def endpointReceive: Receive = {
    case DoStuff => getBusinessActor match {
      case Some(actor) => actor forward DoBusinessStuff
      case None => throw new NoSuchElementException("service is not available right now")
    }
  }

  /**
    *
    * @return random available in cluster actor
    */
  def getBusinessActor : Option[ActorSelection] = {
    val businessActors = clusterMembers.getOrElse("business",List.empty)

    if(businessActors.size == 0) {
      None
    } else {
      Some(businessActors(Random.nextInt(businessActors.size)))
    }
  }

}
