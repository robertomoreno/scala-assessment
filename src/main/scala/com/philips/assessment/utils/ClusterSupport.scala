package com.philips.assessment.utils

import akka.actor.{Actor, ActorSelection, RootActorPath}
import akka.cluster.ClusterEvent._
import akka.cluster.{Cluster, Member}

/**
  * Configure this actor as a cluster node and listening for membership.
  * It's necessary to bind clusterReceive function to the regular actor receive function to listen
  *
  * Create a clusterMembers : Map[String, List[ActorSelection]] (Role -> Members in the cluster with that role)
  */
trait ClusterSupport {
  this: Actor =>

  val cluster = Cluster(context.system)

  override def preStart() = cluster.subscribe(self, classOf[MemberEvent], classOf[ReachabilityEvent])

  override def postStop() = cluster.unsubscribe(self)

  protected var clusterMembers = Map[String, List[ActorSelection]]() //(Role -> Members in the cluster with that role)

  /**
    * Bind to [[akka.actor.Actor.receive]]
    * @return
    */
  protected def clusterReceive: Receive = {
    case state : CurrentClusterState => state.members.foreach(addMember)
    case MemberUp(m) => addMember(m)
    case other: MemberEvent => println(other); removeMember(other.member)
    case UnreachableMember(m) => removeMember(m)
    case ReachableMember(m) => addMember(m)
  }

  private def addMember(member : Member) : Unit = {
    if (!member.roles.isEmpty) {
      // Convention: (head) role is used to label the nodes (single) actor
      val memberName = member.roles.head

      for (rol: String <- member.roles) {
        val actor = context.actorSelection(RootActorPath(member.address) / "user" / memberName)
        if (clusterMembers.contains(rol)) {
          clusterMembers = clusterMembers + (rol -> (actor :: clusterMembers(rol)))
        } else {
          clusterMembers = clusterMembers + (rol -> List(actor))
        }
      }
      println(s"\n\nAdded Node. New state of cluster Members: $clusterMembers \n\n")
    }
  }

  private def removeMember(member : Member) : Unit = {
    if (!member.roles.isEmpty) {
      // Convention: (head) role is used to label the nodes (single) actor
      val memberPath = RootActorPath(member.address)

      for (rol: String <- member.roles) {

        if (clusterMembers.contains(rol)) {
          val newMembers = clusterMembers(rol).filter{_.anchorPath.compareTo(memberPath) != 0 }
          clusterMembers = clusterMembers + (rol -> newMembers)
        }
      }
    }
    println(s"\n\nRemoved Node. New state of cluster Members: $clusterMembers \n\n")
  }

}
