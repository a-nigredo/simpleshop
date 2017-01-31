package dev.nigredo.controller.query

import akka.actor.ActorRef
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.util.Timeout
import dev.nigredo.controller.{Json, _}
import dev.nigredo.dao.query.Pagination
import dev.nigredo.protocol.UserProtocol.Query._
import org.json4s.NoTypeHints
import org.json4s.native.Serialization
import org.json4s.native.Serialization.write

object UserController {

  implicit val formats = Serialization.formats(NoTypeHints)

  def route(dao: ActorRef)(implicit timeout: Timeout) =
    (get & pathEnd) {
      withPagination { (page, perPage) =>
        onSuccess(dao ? UserListRequest(Pagination(page, perPage))) {
          case UserListResponse(data) => Json.Ok(write(data))
        }
      }
    } ~ (pathPrefix(Segment) & get & pathEnd) { id =>
      onSuccess(dao ? UserDetailsRequest(id)) {
        case UserDetailsResponse(None) => Json.NotFound
        case UserDetailsResponse(Some(data)) => Json.Ok(write(data))
      }
    }

}
