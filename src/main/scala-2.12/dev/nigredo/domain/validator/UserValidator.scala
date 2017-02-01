package dev.nigredo.domain.validator

import dev.nigredo.Error.ValidationError
import dev.nigredo.domain.models.{State, User}

import scala.concurrent.Future

object UserValidator {

  def validate[A <: State](user: User with A) =
    if (user.name.value.isEmpty) Left(Future.successful(ValidationError(List("Name can't be empty"))))
    else Right(user)
}
