package dev.nigredo.domain.validator

import dev.nigredo._
import dev.nigredo.domain.models.User.{ExistingUser, NewUser, UpdatedUser}
import dev.nigredo.domain.models.{Email, User}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object NewUserConstraint {

  import UserValidator._

  def apply(emailChecker: Email => Future[Boolean])(user: NewUser) =
    validate[NewUser](List(validateName _, validateEmail _, validateEmailDuplication(emailChecker) _))(user)
}

object UpdatedUserConstraint {

  import UserValidator._

  def apply(emailChecker: Email => Future[Boolean])(oldState: ExistingUser)(newState: UpdatedUser) = {
    val validators = if (oldState.email == newState.email) List(validateName _)
    else List(validateName _, validateEmail _, validateEmailDuplication(emailChecker) _)
    validate[UpdatedUser](validators)(newState)
  }
}

private[this] object UserValidator {

  def validateName(user: User) =
    (if ("""^([\w]{2,})+$""".r.findFirstMatchIn(user.name.value).isEmpty) Some("Name has to be more then 2 symbols and consist of letters and numbers") else None).fs

  def validateEmail(user: User) = {
    val emailRegex = """^[a-zA-Z0-9\.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$""".r
    (if (emailRegex.findFirstMatchIn(user.email.value).isEmpty) Some("Incorrect email format") else None).fs
  }

  def validateEmailDuplication(isEmailExists: Email => Future[Boolean])(user: User) =
    isEmailExists(user.email).map(x => if (x) Some("Email already exists") else None)
}
