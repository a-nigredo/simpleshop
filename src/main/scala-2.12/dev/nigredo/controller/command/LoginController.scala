package dev.nigredo.controller.command

import akka.actor.ActorRef
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.util.Timeout
import de.heikoseeberger.akkahttpjson4s.Json4sSupport._
import dev.nigredo.AuthenticationError
import dev.nigredo.controller.Json
import dev.nigredo.domain.models.Credentials
import dev.nigredo.protocol.SecurityProtocol.{Login, Success}
import org.json4s.DefaultFormats
import org.json4s.native.Serialization
import org.json4s.native.Serialization.write

object LoginController {

  implicit val formats = DefaultFormats
  implicit val serialization = Serialization

  case class Token(token: String)

  def route(actor: ActorRef)(implicit timeout: Timeout) = {
    (post & entity(as[Credentials]) & pathEnd) { credentials =>
      onSuccess(actor ? Login(credentials)) {
        case Success(data) => Json.Ok(write(Token(data.value)))
        case AuthenticationError => Json.Unauthorized
      }
    }
  }
}
