package com.philips.assessment

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.DefaultServlet
import org.eclipse.jetty.webapp.WebAppContext
import org.scalatra.servlet.ScalatraListener

/**
  * Created by roberto on 17/09/2016.
  */
class JettyLauncher {

  def run(serverPort : Int, actorPort : Int) = {

    val server = new Server(serverPort)
    val context = new WebAppContext()
    context setContextPath "/"
    context.setResourceBase("src/main/webapp")
    context.setInitParameter(ScalatraListener.LifeCycleKey, "com.philips.assessment.endpoint.ScalatraBootstrap")
    context.addEventListener(new ScalatraListener)
    context.addServlet(classOf[DefaultServlet], "/")

    context.setAttribute("actorPort",actorPort)

    server.setHandler(context)

    server.start
    server.join
  }
}
