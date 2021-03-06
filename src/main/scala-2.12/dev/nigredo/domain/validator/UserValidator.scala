package dev.nigredo.domain.validator

import dev.nigredo._
import dev.nigredo.domain.models.User.{ExistingUser, NewUser, UpdatedUser}
import dev.nigredo.domain.models.{Email, User}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object NewUserConstraint {

  import UserValidator._

  def apply(emailChecker: EmailChecker)(user: NewUser) =
    validate[NewUser](List(validateName _, validateEmail _, validateEmailDuplication(emailChecker) _))(user)
}

object UpdatedUserConstraint {

  import UserValidator._

  def apply(emailChecker: EmailChecker)(oldState: ExistingUser)(newState: UpdatedUser) = {
    val validators = if (oldState.email == newState.email) List(validateName _)
    else List(validateName _, validateEmail _, validateEmailDuplication(emailChecker) _)
    validate[UpdatedUser](validators)(newState)
  }
}

object UserValidator {

  type EmailChecker = Email => Future[Boolean]

  def validateName(user: User) =
    (if ("""^([\w]{2,})+$""".r.findFirstMatchIn(user.name.value).isEmpty) Option("Name has to be more then 2 symbols and consist of letters and numbers") else Option.empty).fs

  def validateEmail(user: User) = {
    val emailRegex = """^[a-zA-Z0-9\.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$""".r
    (if (emailRegex.findFirstMatchIn(user.email.value).isEmpty) Option("Incorrect email format") else Option.empty).fs
  }

  def validateEmailDuplication(isEmailExists: EmailChecker)(user: User) =
    isEmailExists(user.email).map(x => if (x) Option("Email already exists") else Option.empty)
}
