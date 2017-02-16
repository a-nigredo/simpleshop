package dev.nigredo.actor

import akka.actor.{Actor, Props}
import akka.pattern.pipe
import dev.nigredo.AuthenticationError
import dev.nigredo.domain.models.AccessToken.{NewAccessToken, TokenId, UpdatedAccessToken}
import dev.nigredo.domain.models.{Credentials, ExpireDate}
import dev.nigredo.protocol.SecurityProtocol.{CheckToken, Login, Success}
import dev.nigredo.Types.Command._

import scala.concurrent.ExecutionContext.Implicits.global

private[actor] class SecurityActor(onCreate: Create[Credentials, NewAccessToken],
                                   onUpdate: Update[TokenId, ExpireDate, UpdatedAccessToken]) extends Actor {

  override def receive = {
    case Login(credentials) => onCreate(credentials).map(x => respond(x)((x: NewAccessToken) => Success(x))).pipeTo(sender)
    case CheckToken(value) => onUpdate(value)(ExpireDate()).map(x => respond(x)((x: UpdatedAccessToken) => Success(x))).pipeTo(sender)
  }
}

private[actor] object SecurityActor {
  def props(onCreate: Create[Credentials, NewAccessToken],
            onUpdate: Update[TokenId, ExpireDate, UpdatedAccessToken]) = Props(new SecurityActor(onCreate, onUpdate))
}