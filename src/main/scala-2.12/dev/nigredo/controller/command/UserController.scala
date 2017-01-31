package dev.nigredo.controller.command

import akka.actor.ActorRef
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.util.Timeout
import de.heikoseeberger.akkahttpjson4s.Json4sSupport._
import dev.nigredo.controller.Json
import dev.nigredo.dto.User.CreateUserDto
import dev.nigredo.protocol.UserProtocol.Command.{CreateUser, Created, Updated}
import org.json4s.DefaultFormats
import org.json4s.native.Serialization
import org.json4s.native.Serialization.write

object UserController {

  implicit val formats = DefaultFormats
  implicit val serialization = Serialization

  case class Id(id: String)

  def route(actor: ActorRef)(implicit timeout: Timeout) =
    (post & entity(as[CreateUserDto]) & pathEnd) { user =>
      onSuccess(actor ? CreateUser(user)) {
        case Created(data) => Json.Ok(write(Id(data.value)))
        case Updated(data) => Json.Ok(write(Id(data.value)))
      }
    }
}
