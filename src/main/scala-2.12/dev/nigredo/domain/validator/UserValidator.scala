package dev.nigredo.domain.validator

import dev.nigredo.Error.ValidationError
import dev.nigredo.domain.models.{State, User}

import scala.concurrent.Future

object UserValidator {

  def validate[A <: State](user: User with A) = {

    val nameValidator = if ("""^([\w]{2,})+$""".r.findFirstMatchIn(user.name.value).isEmpty) Some("Name has to be more then 2 symbols and consist of letters and numbers") else None
    val emailValidator = {
      val emailRegex = """^[a-zA-Z0-9\.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$""".r
      if (emailRegex.findFirstMatchIn(user.email.value).isEmpty) Some("Incorrect email format") else None
    }

    List(nameValidator, emailValidator).filter(_.isDefined) match {
      case l@x :: xs => Left(Future.successful(ValidationError(l.map(_.get))))
      case Nil => Right(user)
    }
  }
}
