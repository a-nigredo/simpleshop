package dev.nigredo.service.user

import akka.actor.{Actor, Props}
import akka.pattern.pipe
import dev.nigredo.domain.models.User.{NewUser, UpdatedUser}
import dev.nigredo.protocol.UserProtocol.Command._
import dev.nigredo.service.Service.{Create, Update}
import dev.nigredo.service.createResponse

import scala.concurrent.ExecutionContext.Implicits.global

private[service] class UserServiceActor(onCreate: Create, onUpdate: Update) extends Actor {

  override def receive: Receive = {
    case CreateUser(dto) => onCreate(dto).map(x => createResponse(x, (x: NewUser) => Created(x.id))).pipeTo(sender)
    case UpdateUser(id, dto) => onUpdate(id)(dto).map(x => createResponse(x, (x: UpdatedUser) => Updated(x.id))).pipeTo(sender)
  }
}

private[service] object UserServiceActor {
  def props(onCreate: Create, onUpdate: Update) = Props(new UserServiceActor(onCreate, onUpdate))
}