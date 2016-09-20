package com.philips.assessment.utils.actors

import akka.actor.{Actor, ActorRef, ActorSelection, RootActorPath}
import akka.cluster.ClusterEvent._
import akka.cluster.Member
import com.philips.assessment.utils.ClusterSupport

import scala.util.Random


/**
  * Created by roberto on 20/09/2016.
  */
trait ActorSelector {

  def selectActorRef(): Option[ActorRef]

  def selectActorPath(): Option[ActorSelection]
}

trait RandomClusterSelector extends ActorSelector with ClusterSupport {

  this: Actor =>

  protected var clusterMembers = Map[String, List[ActorSelection]]() //(Role -> Members in the cluster with that role)

  override def onInitialState(state: CurrentClusterState) = {
    super.onInitialState(state)
    state.members.foreach(addMember)
  }

  override def onMemberUp(member: Member) = {
    super.onMemberUp(member)
    addMember(member)
  }

  override def onNonMemberUp(event: MemberEvent) = {
    super.onNonMemberUp(event)
    removeMember(event.member)
  }

  override def onUnreachableMember(member: Member) = {
    super.onUnreachableMember(member)
    removeMember(member)
  }

  override def onReachableMember(member: Member) = {
    super.onReachableMember(member)
    addMember(member)
  }

  /**
    * Not implemented
    *
    * @throws NotImplementedError
    */
  override def selectActorRef(): Option[ActorRef] = ???

  override def selectActorPath(): Option[ActorSelection] = {
    println("selectActorPath")
    val businessActors = clusterMembers.getOrElse("business", List.empty)

    if (businessActors.size == 0) {
      None
    } else {
      Some(businessActors(Random.nextInt(businessActors.size)))
    }
  }

  private def addMember(member: Member): Unit = {
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

  private def removeMember(member: Member): Unit = {
    if (!member.roles.isEmpty) {
      // Convention: (head) role is used to label the nodes (single) actor
      val memberPath = RootActorPath(member.address)

      for (rol: String <- member.roles) {

        if (clusterMembers.contains(rol)) {
          val newMembers = clusterMembers(rol).filter {
            _.anchorPath.compareTo(memberPath) != 0
          }
          clusterMembers = clusterMembers + (rol -> newMembers)
        }
      }
    }
    println(s"\n\nRemoved Node. New state of cluster Members: $clusterMembers \n\n")
  }
}
