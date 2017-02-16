package dev.nigredo.controller.command

import akka.actor.ActorRef
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.util.Timeout
import de.heikoseeberger.akkahttpjson4s.Json4sSupport._
import dev.nigredo.{ItemNotFound, ValidationError}
import dev.nigredo.controller.Json
import dev.nigredo.domain.models.Uuid
import dev.nigredo.dto.User.{CreateUserDto, UpdateUserDto}
import dev.nigredo.protocol.UserProtocol.Command._
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
        case ValidationError(errs) => Json.BadRequest(write(errs))
      }
    } ~ (put & pathPrefix(JavaUUID) & entity(as[UpdateUserDto]) & pathEnd) { (id, user) =>
      onSuccess(actor ? UpdateUser(Uuid(id.toString), user)) {
        case Updated(data) => Json.Ok(write(Id(data.value)))
        case ValidationError(errs) => Json.BadRequest(write(errs))
        case ItemNotFound => Json.NotFound
      }
    } ~ (delete & pathPrefix(JavaUUID) & pathEnd) { id =>
      actor ! DeleteUser(Uuid(id.toString))
      Json.Ok()
    }
}
