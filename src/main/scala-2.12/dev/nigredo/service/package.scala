package dev.nigredo

import dev.nigredo.Error.{InternalError, ItemNotFound, ValidationError}
import dev.nigredo.protocol.ApplicationProtocol
import dev.nigredo.protocol.ApplicationProtocol.{ApplicationProtocol, InvalidData}

import scalaz.{-\/, \/-}

package object service {

  private[service] def createResponse[A, B <: ApplicationProtocol](response: Result[A], onSuccess: A => B) =
    response match {
      case -\/(err) => err match {
        case err@ValidationError(_) => InvalidData(err)
        case err@InternalError(_) => ApplicationProtocol.InternalError(err)
        case ItemNotFound() => ApplicationProtocol.ItemNotFound
        case _ => ApplicationProtocol.InternalError(InternalError("Something goes wrong"))
      }
      case \/-(user) => onSuccess(user)
    }
}
