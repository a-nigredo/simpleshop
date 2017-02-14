package dev.nigredo.service.user

import akka.actor.{Actor, Props}
import akka.pattern.pipe
import dev.nigredo.dao.command.Types
import dev.nigredo.domain.models.User
import dev.nigredo.domain.models.User.{NewUser, UpdatedUser}
import dev.nigredo.dto.User.{CreateUserDto, UpdateUserDto}
import dev.nigredo.protocol.UserProtocol.Command._
import dev.nigredo.service.Types._
import dev.nigredo.service.respond

import scala.concurrent.ExecutionContext.Implicits.global

private[service] class UserServiceActor(onCreate: Create[CreateUserDto, NewUser],
                                        onUpdate: Update[User.UserId, UpdateUserDto, UpdatedUser],
                                        onDelete: Types.Delete[String]) extends Actor {
  override def receive: Receive = {
    case CreateUser(dto) => onCreate(dto).map(x => respond(x, (x: NewUser) => Created(x.id))).pipeTo(sender)
    case UpdateUser(id, dto) => onUpdate(id)(dto).map(x => respond(x, (x: UpdatedUser) => Updated(x.id))).pipeTo(sender)
    case DeleteUser(id) => onDelete(id)
  }
}

private[service] object UserServiceActor {
  def props(onCreate: Create[CreateUserDto, NewUser],
            onUpdate: Update[User.UserId, UpdateUserDto, UpdatedUser],
            onDelete: Types.Delete[String]) = Props(new UserServiceActor(onCreate, onUpdate, onDelete))
}