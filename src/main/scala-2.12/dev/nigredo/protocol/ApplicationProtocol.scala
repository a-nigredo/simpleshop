package dev.nigredo.protocol

import dev.nigredo.Error.ValidationError

object ApplicationProtocol {

  trait ApplicationProtocol

  case class InternalError(data: dev.nigredo.Error.InternalError) extends ApplicationProtocol

  case object ItemNotFound extends ApplicationProtocol

  case class InvalidData(err: ValidationError) extends ApplicationProtocol

}