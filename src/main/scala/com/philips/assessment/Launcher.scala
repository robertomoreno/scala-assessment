package com.philips.assessment

import com.philips.assessment.business.BusinessLauncher
import com.philips.assessment.endpoint.JettyLauncher

/**
  * Created by roberto on 17/09/2016.
  */
object Launcher {

  def main(args: Array[String]): Unit = {

    if (!args.isEmpty) {
      try {
        val List(mode , port) = args.toList

        mode match {
          case "endpoint" => runEndpoint(port)
          case "business" => runBusiness(port)
          case _ => printHelp
        }
      }
      catch {
        case e: Throwable =>
          e.printStackTrace()
          printHelp
      }
    } else {
      runEndpoint("8080")
      runBusiness("2551")
      runBusiness("2552")
    }
  }

  def runBusiness(port:String) =  new BusinessLauncher().run(port.toInt)

  def runEndpoint(port : String) =  new JettyLauncher().run(port.toInt)

  def printHelp = println(
    """
      | - endpoint [serverPort,actorPort]
      | - business [actorPort]
    """.stripMargin
  )

}
