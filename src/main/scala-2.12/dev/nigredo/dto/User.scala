package dev.nigredo.dto

object User {

  case class CreateUserDto(name: String, password: String, email: String)

  case class UpdateUserDto(name: Option[String], password: Option[String], email: Option[String], active: Option[Int])

}
