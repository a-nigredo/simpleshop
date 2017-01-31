package dev.nigredo.dao.command.user

import akka.actor.{Actor, Props}
import dev.nigredo.dao.command.{Create, Update}
import dev.nigredo.domain.models.User.{ExistingUserId, NewUser, NewUserId, UpdatedUser}

class DaoActor(create: Create[NewUser], update: Update[UpdatedUser, ExistingUserId]) extends Actor {
  override def receive: Receive = ???
}

object DaoActor {
  def props(create: Create[NewUser], update: Update[UpdatedUser, ExistingUserId]) = Props(new DaoActor(create, update))
}