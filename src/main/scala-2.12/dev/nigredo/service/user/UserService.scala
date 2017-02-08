package dev.nigredo.service.user

import dev.nigredo.domain.models
import dev.nigredo.domain.models.User.{ExistingUser, NewUser, UpdatedUser, UserId}
import dev.nigredo.domain.models._
import dev.nigredo.domain.validator.{NewUserValidator, UpdatedUserValidator}
import dev.nigredo.dto.User.{CreateUserDto, UpdateUserDto}
import dev.nigredo.system

private[service] object UserService {

  import com.github.t3hnar.bcrypt._
  import dev.nigredo.dao.Dao.Mongo._
  import dev.nigredo.service.Service._

  private[this] val onCreate =
    create[CreateUserDto, NewUser](dto => User(Name(dto.name), Email(dto.email), models.Password(dto.password.bcrypt)))(NewUserValidator(isEmailExists))(createUser)
  private[this] val onUpdate =
    update[UserId, UpdateUserDto, ExistingUser, UpdatedUser](findUserById)((x: ExistingUser) => (dto: UpdateUserDto) =>
      x.update((dto.name.map(Name), dto.email.map(Email), dto.password.map(x => models.Password(x.bcrypt)), dto.active.map(x => Activation(x)))))(UpdatedUserValidator(isEmailExists))(updateUser)

  def apply() = system.actorOf(UserServiceActor.props(onCreate, onUpdate, deleteUser))
}
