package dev.nigredo.service

import dev.nigredo.domain.models.User.{ExistingUser, NewUser, UpdatedUser, UserId}
import dev.nigredo.domain.validator.{NewUserConstraint, UpdatedUserConstraint}
import dev.nigredo.dto.User
import dev.nigredo.dto.User.{CreateUserDto, UpdateUserDto}
import dev.nigredo.service.user.UserServiceActor
import dev.nigredo.system

object Service {

  def userService = {

    import dev.nigredo.dao.Dao.Mongo._

    val onCreate = create[CreateUserDto, NewUser](User.map)(NewUserConstraint(isEmailExists))(saveUser)
    val onUpdate = update[UserId, UpdateUserDto, ExistingUser, UpdatedUser](findUserById)(User.map)(UpdatedUserConstraint(isEmailExists))(updateUser)
    system.actorOf(UserServiceActor.props(onCreate, onUpdate, deleteUser))
  }

}
