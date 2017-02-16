package dev.nigredo.service.impl

import dev.nigredo.domain.models.User.{ExistingUser, NewUser, UpdatedUser, UserId}
import dev.nigredo.domain.models.{Credentials, ExpireDate}
import dev.nigredo.domain.validator.{NewUserConstraint, UpdatedUserConstraint}
import dev.nigredo.dto.User
import dev.nigredo.dto.User.{CreateUserDto, UpdateUserDto}

object Mongo {

  import dev.nigredo.dao.impl.Mongo._
  import dev.nigredo.service.Flow._

  def createUser = create[CreateUserDto, NewUser](User.map)(NewUserConstraint(isEmailExists))(addUser)

  def saveUser = update[UserId, UpdateUserDto, ExistingUser, UpdatedUser](findUserById)(User.map)(UpdatedUserConstraint(isEmailExists))(updateUser)

  def login(credentials: Credentials) =
    dev.nigredo.service.SecurityService.login(credentials)(findActiveUserByEmail)(addToken)

  def prolongToken(expireDate: => ExpireDate = ExpireDate())(value: String) =
    dev.nigredo.service.SecurityService.prolongToken(value)(expireDate)(findTokenByValue)(removeToken)(saveToken)
}