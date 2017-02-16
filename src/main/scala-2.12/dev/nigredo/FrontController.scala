package dev.nigredo

import akka.actor.ActorRef
import akka.http.scaladsl.server.Directives.{pathPrefix, _}
import akka.http.scaladsl.server.{Directive1, Route}
import akka.pattern.ask
import akka.util.Timeout
import dev.nigredo.controller.Json
import dev.nigredo.controller.query.UserController
import dev.nigredo.domain.models.AccessToken
import dev.nigredo.protocol.SecurityProtocol.{CheckToken, Success}

import scala.concurrent.duration._

object FrontController {

  import dev.nigredo.actor.impl.Mongo._

  implicit val timeout = Timeout(5 seconds)

  lazy val auth = security

  def authenticated(service: ActorRef): Directive1[AccessToken] = headerValueByName("X-Auth-Token").flatMap { token =>
    onSuccess(service ? CheckToken(token)).flatMap {
      case Success(t) => provide(t)
      case AuthenticationError => Json.Unauthorized
    }
  }

  val routes = pathPrefix("api") {
    pathPrefix("login")(controller.command.LoginController.route(auth)) ~ Route.seal {
      authenticated(auth) { at =>
        pathPrefix("user") {
          UserController.route(dao.impl.Mongo.findUser) ~ controller.command.UserController.route(user)
        }
      }
    }
  }

}
