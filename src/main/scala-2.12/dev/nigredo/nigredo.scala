package dev

import java.io.File

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory

import scala.concurrent.Future
import scala.sys.SystemProperties

package object nigredo {

  type Result[A] = Either[Future[Error.Error], A]

  implicit val system = ActorSystem("recruiting-system")
  implicit val materializer = ActorMaterializer()

  def config = {
    val defaultConfig = ConfigFactory.load()
    new SystemProperties().get("config")
      .map(x => ConfigFactory.parseFile(new File(x)).withFallback(defaultConfig))
      .getOrElse(defaultConfig)
  }

  val dataSourceConfig = config.getConfig("dataSource")
  val serverConfig = config.getConfig("server")

}