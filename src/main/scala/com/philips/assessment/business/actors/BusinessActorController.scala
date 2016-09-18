package com.philips.assessment.business.actors

import akka.actor.Actor
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.{MemberEvent, ReachabilityEvent}
import com.philips.assessment.business.actors.BusinessActorController.{DoBusinessStuff, StuffDone}


object BusinessActorController{
    trait BusinessMessage
    case object DoBusinessStuff extends BusinessMessage
    case class StuffDone(stuff : String) extends BusinessMessage
}

class BusinessActorController extends Actor{

  val cluster = Cluster(context.system)

  override def preStart() = cluster.subscribe(self, classOf[MemberEvent], classOf[ReachabilityEvent])

  override def postStop() = cluster.unsubscribe(self)

  override def receive : Receive = {
    case DoBusinessStuff => sender ! StuffDone(s"cool stuff done from $self to $sender!")
  }
}
