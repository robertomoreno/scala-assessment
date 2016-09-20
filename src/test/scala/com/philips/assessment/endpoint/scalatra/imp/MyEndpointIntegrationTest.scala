package com.philips.assessment.endpoint.scalatra.imp

import akka.actor.{ActorRef, PoisonPill}
import com.philips.assessment.business.BusinessLauncher
import org.eclipse.jetty.servlet.ServletHolder
import org.scalatest.FunSuiteLike
import org.scalatra.test.scalatest.ScalatraSuite

/**
  * Created by roberto on 18/09/2016.
  */
class MyEndpointIntegrationTest extends ScalatraSuite with FunSuiteLike {

  var businessNodes : List[ActorRef] = List.empty
  var servlet : ServletHolder= new ServletHolder()

  override def beforeAll(): Unit = {
    super.beforeAll()
    businessNodes = new BusinessLauncher().run(2551) :: new BusinessLauncher().run(2552) :: businessNodes
    servlet = addServlet(classOf[MyEndpoint], "/*")
    Thread.sleep(5000)
  }

  override def afterAll(): Unit = {
    super.afterAll()
    killAkkaCLusterNodes()
  }

  test("Correct path with service"){
    get("/") {
      status should equal(200)
    }
  }


  def killAkkaCLusterNodes() = {
    servlet.getServlet.asInstanceOf[MyEndpoint].actorController ! PoisonPill
    businessNodes.foreach(node => node ! PoisonPill)
  }
}
