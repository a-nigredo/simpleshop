package dev.nigredo.domain.models

import dev.nigredo.domain.models

case class User private(id: Id[String], name: Name, email: Email, password: Password)
  extends Persistent[String] with User.Existing

object User {

  type NewUser = User with New
  type UpdatedUser = User with Updated
  type ExistingUser = User with Existing
  type UserId = Id[String]

  def apply(name: Name, email: Email, password: Password) = new User(Uuid(), name, email, password) with New

  sealed trait Existing extends models.Existing[User, (Option[Name], Option[Email], Option[Password])] {
    this: User =>

    override def update(updateWith: (Option[Name], Option[Email], Option[Password])) = {
      val (name, email, password) = updateWith
      new User(this.id, name.getOrElse(this.name), email.getOrElse(this.email), password.getOrElse(this.password)) with Updated
    }
  }

}

case class Email(value: String) extends AnyVal

case class Password(value: String) extends AnyVal

case class Name(value: String) extends AnyVal
