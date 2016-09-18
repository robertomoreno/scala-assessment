package com.philips.assessment.endpoint

import com.philips.assessment.JettyLauncher
import com.philips.assessment.business.BusinessLauncher

/**
  * Created by roberto on 17/09/2016.
  */
object Launcher {

  def main(args: Array[String]): Unit = {

    try{
      val mode :: options = args.toList

      mode match {
        case "endpoint" => runEndpoint(options)
        case "business" => runBusiness(options)
        case _ => printHelp
      }
    }
    catch {
      case e : Throwable =>
        e.printStackTrace()
        printHelp
    }
  }

  def runBusiness(options:List[String]) = {
    val List(actorPort) = options
    new BusinessLauncher().run(actorPort.toInt)
  }

  def runEndpoint(options : List[String]) = {
    val List(serverPort, actorPort) = options
    new JettyLauncher().run(serverPort.toInt, actorPort.toInt)
  }

  def printHelp = println(
    """
      | - endpoint [serverPort,actorPort]
      | - business [actorPort]
    """.stripMargin
  )

}
