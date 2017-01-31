package dev.nigredo.domain.validator

import dev.nigredo.Error.ValidationError
import dev.nigredo.domain.models.{Id, User}
import dev.nigredo.domain.models.User.State

import scala.concurrent.Future

object UserValidator {

  def validate[A <: State, B <: Id.State](user: User[A, B]) =
    if (user.name.isEmpty)
      Left(Future.successful(ValidationError(List("error"))))
    else
      Right(user)
}
