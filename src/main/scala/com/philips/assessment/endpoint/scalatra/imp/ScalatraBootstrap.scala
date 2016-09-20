package com.philips.assessment.endpoint.scalatra.imp

import javax.servlet.ServletContext

import org.scalatra.LifeCycle

/**
  * Created by roberto on 17/09/2016.
  */
class ScalatraBootstrap extends LifeCycle {
  override def init(context: ServletContext) {
    context.mount(new MyEndpoint(), "/*")
  }
}
