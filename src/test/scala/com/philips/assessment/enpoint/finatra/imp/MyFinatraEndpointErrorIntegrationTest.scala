package com.philips.assessment.enpoint.finatra.imp

import com.philips.assessment.endpoint.finatra.imp.FinatraServer
import com.twitter.finagle.http.Status
import com.twitter.finatra.http.test.EmbeddedHttpServer
import com.twitter.inject.server.FeatureTest

/**
  * Created by roberto on 19/09/2016.
  */
class MyFinatraEndpointErrorIntegrationTest extends FeatureTest {

  override val server = new EmbeddedHttpServer(new FinatraServer)

  "FinatraServer" should  {

    "return an error when business backend is disabled" in {
      server.httpGet(
        path = "/",
        andExpect = Status.InternalServerError)
    }
  }

}
