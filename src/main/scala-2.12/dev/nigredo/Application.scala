package dev.nigredo

import akka.http.scaladsl.Http
import com.typesafe.scalalogging.Logger

import scala.concurrent.ExecutionContext.Implicits.global
import org.slf4j.LoggerFactory

import scala.util.Failure

object Application extends App {

  val logger = Logger(LoggerFactory.getLogger(this.getClass.getSimpleName))
  val host = serverConfig.getString("host")
  val port = serverConfig.getInt("port")

  Http().bindAndHandle(FrontController.routes, host, port).onComplete {
    case Failure(ex) => logger.error(s"Failed to bind to $host:$port - $ex")
    case _ => logger.info(s"Server has been started successfully on host '$host' and port $port")
  }
}
