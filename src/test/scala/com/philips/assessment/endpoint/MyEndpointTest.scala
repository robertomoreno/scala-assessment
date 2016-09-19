package com.philips.assessment.endpoint

import org.scalatest.FunSuiteLike
import org.scalatra.test.scalatest.ScalatraSuite

/**
  * Created by roberto on 18/09/2016.
  */
class MyEndpointTest extends ScalatraSuite with FunSuiteLike {

  addServlet(classOf[MyEndpoint], "/*")

  test("Wrong path"){
    get("/wrongPath") {
      status should equal(400)
      body should include ("what are you looking for?")
    }
  }

}
