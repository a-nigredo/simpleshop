package dev.nigredo.domain.validator

import dev.nigredo.domain.models.{Email, State, User}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import dev.nigredo._

object UserValidator {

  def validateNameFormat(user: User with State) =
    (if ("""^([\w]{2,})+$""".r.findFirstMatchIn(user.name.value).isEmpty) Some("Name has to be more then 2 symbols and consist of letters and numbers") else None).fs

  def validateEmailFormat(user: User with State) = {
    val emailRegex = """^[a-zA-Z0-9\.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$""".r
    (if (emailRegex.findFirstMatchIn(user.email.value).isEmpty) Some("Incorrect email format") else None).fs
  }

  def validateEmailDuplication(isEmailExists: Email => Future[Boolean])(user: User with State) =
    isEmailExists(user.email).map(x => if (x) Some("Email already exists") else None)

  def apply[A <: State](isEmailExists: Email => Future[Boolean]) = validate[User with A](List(validateNameFormat _, validateEmailFormat _, validateEmailDuplication(isEmailExists))) _
}
