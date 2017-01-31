package dev.nigredo

import dev.nigredo.Error.{InternalError, ItemNotFound, ValidationError}
import dev.nigredo.protocol.ApplicationProtocol
import dev.nigredo.protocol.ApplicationProtocol.{ApplicationProtocol, InvalidData}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

package object service {

  def createResponse[A, B <: ApplicationProtocol](response: Result[Future[A]], onSuccess: A => B) =
    response match {
      case Left(err) => err.map {
        case err: ValidationError => InvalidData(err)
        case err: InternalError => ApplicationProtocol.InternalError(err)
        case _: ItemNotFound => ApplicationProtocol.ItemNotFound
        case _ => ApplicationProtocol.InternalError(InternalError("Something goes wrong"))
      }
      case Right(user) => user.map(onSuccess)
    }
}
