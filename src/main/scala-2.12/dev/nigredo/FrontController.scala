package dev.nigredo

import akka.http.scaladsl.server.Directives.{pathPrefix, _}
import akka.util.Timeout
import dev.nigredo.controller.query.UserController
import dev.nigredo.dao.Dao

import scala.concurrent.duration._

object FrontController {

  implicit val timeout = Timeout(15 seconds)

  val routes = pathPrefix("api" / "user") {
    UserController.route(Dao.Mongo.queryUserDao) ~ controller.command.UserController.route(service.Service.userService)
  }

}
