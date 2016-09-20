package com.philips.assessment.endpoint.scalatra.imp

import akka.actor.PoisonPill
import org.eclipse.jetty.servlet.ServletHolder
import org.scalatest.FunSuiteLike
import org.scalatra.test.scalatest.ScalatraSuite


class MyEndpointErrorIntegrationTest extends ScalatraSuite with FunSuiteLike {

  var servlet : ServletHolder= new ServletHolder()

  override def beforeAll(): Unit = {
    super.beforeAll()
    servlet = addServlet(classOf[MyEndpoint], "/*")
  }

  override def afterAll(): Unit = {
    super.afterAll()
    killEndpointNode()
  }

  test("Wrong path"){
    get("/wrongPath") {
      status should equal(400)
      body should include ("what are you looking for?")
    }
  }

  test("Correct path with no service"){
    get("/") {
      status should equal(500)
    }
  }


  def killEndpointNode() = {
    servlet.getServlet.asInstanceOf[MyEndpoint].actorController ! PoisonPill
  }
}
