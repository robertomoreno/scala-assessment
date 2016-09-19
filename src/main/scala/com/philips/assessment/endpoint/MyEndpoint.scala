package com.philips.assessment.endpoint

import akka.actor.{ActorSystem, Props}
import akka.pattern.{AskTimeoutException, ask}
import akka.util.Timeout
import com.philips.assessment.business.actors.BusinessActorController.StuffDone
import com.philips.assessment.endpoint.actors.EndpointActorController
import com.philips.assessment.endpoint.actors.EndpointActorController.{DoStuff, EndpointMessage}
import com.typesafe.config.ConfigFactory
import org.scalatra._

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by roberto on 17/09/2016.
  */
class MyEndpoint extends ScalatraServlet with FutureSupport {

  val system = getActorSystem()

  val actorController = system.actorOf(Props[EndpointActorController], name = "endpoint")

  override protected implicit def executor: ExecutionContext = system.dispatcher
  implicit val timeout = new Timeout(1 seconds)

  get("/") {
    doStuff( DoStuff ).map(Ok(_)).recover{ case e => InternalServerError(e.getMessage) }
  }

  notFound {
    BadRequest ("what are you looking for?")
  }

  /**
    * Send a message to EndpointActorController in order to do some business logic.
    * If TimeoutException is thrown, will try 3 more times.
    *
    * @param action
    * @param retries
    * @return
    */
  def doStuff(action: EndpointMessage, retries: Int = 0): Future[StuffDone] = {
    (actorController ? DoStuff)
      .mapTo[StuffDone]
      .recoverWith {
        case e : AskTimeoutException if retries < 3 =>
          println("TimeoutException "+ retries)
          doStuff(action, retries + 1)
        case e => throw e
      }
  }

  def getActorSystem() = {

    val clusterConfiguration =
      ConfigFactory.parseString(s"akka.remote.netty.tcp.port=0")
        .withFallback(ConfigFactory.parseString("akka.cluster.roles = [endpoint]"))
        .withFallback(ConfigFactory.load())

    ActorSystem("ClusterSystem", clusterConfiguration)
  }
}
