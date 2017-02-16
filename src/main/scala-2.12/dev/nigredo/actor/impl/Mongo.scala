package dev.nigredo.actor.impl

import dev.nigredo.actor.{SecurityActor, UserActor}

object Mongo {

  import dev.nigredo._
  import dev.nigredo.service.impl.Mongo._
  import dev.nigredo.dao.impl.Mongo._

  def user = system.actorOf(UserActor.props(createUser, saveUser, deleteUser))

  def security = system.actorOf(SecurityActor.props(createToken, updateToken))
}
