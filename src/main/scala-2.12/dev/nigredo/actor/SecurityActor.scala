package dev.nigredo.actor

import akka.actor.{Actor, Props}
import akka.pattern.pipe
import dev.nigredo.domain.models.AccessToken.{NewToken, UpdatedToken}
import dev.nigredo.protocol.SecurityProtocol.{CheckToken, Login, Success}
import dev.nigredo.service.Types.{CreateToken, UpdateToken}

import scala.concurrent.ExecutionContext.Implicits.global

private[actor] class SecurityActor(onCreate: CreateToken, onUpdate: UpdateToken)
  extends Actor {
  override def receive = {
    case Login(credentials) => onCreate(credentials).map(x => respond(x)((x: NewToken) => Success(x))).pipeTo(sender)
    case CheckToken(value) => onUpdate(value).map(x => respond(x)((x: UpdatedToken) => Success(x))).pipeTo(sender)
  }
}

private[actor] object SecurityActor {
  def props(onCreate: CreateToken, onUpdate: UpdateToken) =
    Props(new SecurityActor(onCreate, onUpdate))
}