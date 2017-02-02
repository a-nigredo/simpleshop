package dev.nigredo.service

import dev.nigredo.domain.models
import dev.nigredo.domain.models.User.ExistingUser
import dev.nigredo.domain.models._
import dev.nigredo.domain.validator.UserValidator
import dev.nigredo.dto.User.UpdateUserDto
import dev.nigredo.service.user.{UserService, UserServiceActor}

object Service {

  import dev.nigredo._
  import dev.nigredo.dao.Dao.Mongo._
  import com.github.t3hnar.bcrypt._

  private val onCreate = UserService.create(dto => User(Name(dto.name), Email(dto.email), models.Password(dto.password.bcrypt)))(UserValidator.validate)(createUser)
  private val onUpdate = UserService.update(findUserById)((x: ExistingUser) => (dto: UpdateUserDto) =>
    x.update((dto.name.map(Name), dto.email.map(Email), dto.password.map(x => models.Password(x.bcrypt)), dto.active.map(x => Activation(x)))))(UserValidator.validate)(updateUser)

  def userService = system.actorOf(UserServiceActor.props(onCreate, onUpdate))
}
