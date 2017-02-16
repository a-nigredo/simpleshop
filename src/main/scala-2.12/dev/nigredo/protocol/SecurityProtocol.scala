package dev.nigredo.protocol

import dev.nigredo.domain.models.{AccessToken, Credentials}

object SecurityProtocol {

  case class CheckToken(value: String) extends ApplicationProtocol

  case class Login(credentials: Credentials) extends ApplicationProtocol

  case class Success(token: AccessToken) extends ApplicationProtocol

}
