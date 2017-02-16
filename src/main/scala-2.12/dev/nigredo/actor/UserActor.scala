package dev.nigredo.actor

import akka.actor.{Actor, Props}
import akka.pattern.pipe
import dev.nigredo.Types.Command._
import dev.nigredo.domain.models.User
import dev.nigredo.domain.models.User.{NewUser, UpdatedUser}
import dev.nigredo.dto.User.{CreateUserDto, UpdateUserDto}
import dev.nigredo.protocol.UserProtocol.Command._

import scala.concurrent.ExecutionContext.Implicits.global

private[actor] class UserActor(create: Create[CreateUserDto, NewUser],
                               update: Update[User.UserId, UpdateUserDto, UpdatedUser],
                               delete: Delete[User.UserId]) extends Actor {
  override def receive: Receive = {
    case CreateUser(dto) => create(dto).map(x => respond(x)((x: NewUser) => Created(x.id))).pipeTo(sender)
    case UpdateUser(id, dto) => update(id)(dto).map(x => respond(x)((x: UpdatedUser) => Updated(x.id))).pipeTo(sender)
    case DeleteUser(id) => delete(id)
  }
}

private[actor] object UserActor {
  def props(create: Create[CreateUserDto, NewUser],
            update: Update[User.UserId, UpdateUserDto, UpdatedUser],
            delete: Delete[User.UserId]) = Props(new UserActor(create, update, delete))
}