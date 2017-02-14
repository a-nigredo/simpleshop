package dev.nigredo.domain.models

sealed trait Password {
  val value: String
}

case class BcryptedPassword private(value: String) extends Password

object Password {

  def bcrypt(value: String) = {
    BcryptedPassword(com.github.t3hnar.bcrypt.Password(value).bcrypt)
  }

  def bcrypted(value: String) = BcryptedPassword(value)
}