package com.philips.assessment.endpoint

import akka.actor.PoisonPill
import org.scalatest.FunSuiteLike
import org.scalatra.test.scalatest.ScalatraSuite

/**
  * Created by roberto on 18/09/2016.
  */
class MyEndpointTest extends ScalatraSuite with FunSuiteLike {

  override def afterAll(): Unit = {
    super.afterAll()
    finishEndpointNode()
  }

  val servlet = addServlet(classOf[MyEndpoint], "/*")

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


  def finishEndpointNode() = servlet.getServlet.asInstanceOf[MyEndpoint].actorController ! PoisonPill
}
