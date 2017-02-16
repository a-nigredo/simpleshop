package dev.nigredo.domain.models

sealed trait Password {
  val value: String
  val salt: Salt
}

case class Salt(value: String) extends AnyVal

case class BcryptedPassword private(value: String, salt: Salt) extends Password

object Password {

  def bcrypt(value: String, salt: Salt = Salt(com.github.t3hnar.bcrypt.generateSalt.toString)) =
    BcryptedPassword(com.github.t3hnar.bcrypt.Password(value).bcrypt(salt.value), salt)

  def bcrypted(value: String, salt: Salt) = BcryptedPassword(value, salt)
}