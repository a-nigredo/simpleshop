package dev.nigredo.service.impl

import dev.nigredo.domain.models.User.{ExistingUser, NewUser, UpdatedUser, UserId}
import dev.nigredo.domain.models.{AccessToken, Credentials, ExpireDate, Password}
import dev.nigredo.domain.validator.{NewUserConstraint, UpdatedUserConstraint}
import dev.nigredo.dto.User
import dev.nigredo.dto.User.{CreateUserDto, UpdateUserDto}
import dev.nigredo.{AuthenticationError, _}

import scala.concurrent.ExecutionContext.Implicits.global
import scalaz.Scalaz._
import scalaz.{-\/, OptionT, \/-}

object Mongo {

  import dev.nigredo.dao.impl.Mongo._
  import dev.nigredo.service.Flow._

  def createUser = create[CreateUserDto, NewUser](User.map)(NewUserConstraint(isEmailExists))(addUser)

  def saveUser = update[UserId, UpdateUserDto, ExistingUser, UpdatedUser](findUserById)(User.map)(UpdatedUserConstraint(isEmailExists))(updateUser)

  def createToken(credentials: Credentials) = OptionT(findActiveUserByEmail(credentials.email)).flatMapF { user =>
    if (user.password.value == Password.bcrypt(credentials.password, user.password.salt).value) addToken(AccessToken(user.id)).map(x => \/-(x))
    else -\/(AuthenticationError).fs
  }.getOrElse(-\/(AuthenticationError))

  def updateToken(value: String)(expireDate: ExpireDate) = OptionT(findTokenByValue(value)).flatMapF { token =>
    if (token.isExpired) removeToken(value).map(x => -\/(AuthenticationError))
    else saveToken(token.update(expireDate)).map(x => \/-(x))
  }.getOrElse(-\/(AuthenticationError))
}