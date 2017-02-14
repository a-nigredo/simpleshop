package dev.nigredo.dto

import dev.nigredo.domain.models
import dev.nigredo.domain.models.User.ExistingUser
import dev.nigredo.domain.models.{Activation, Email, Name}

object User {

  case class CreateUserDto(name: String, password: String, email: String)

  case class UpdateUserDto(name: Option[String], password: Option[String], email: Option[String], active: Option[Int])

  def map(dto: CreateUserDto) = models.User(Name(dto.name), Email(dto.email), models.Password.bcrypt(dto.password))

  def map(user: ExistingUser)(dto: UpdateUserDto) = {
    val updateWith = (dto.name.map(Name), dto.email.map(Email), dto.password.map(x => models.Password.bcrypt(x)), dto.active.map(x => Activation(x)))
    user.update(updateWith)
  }
}
