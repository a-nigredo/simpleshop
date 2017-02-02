package dev.nigredo.domain.models

import java.util.Date

import dev.nigredo.domain.models

case class User private(id: Id[String], name: Name, email: Email, password: Password, active: Activation, creationDate: Date, modificationDate: Option[Date])
  extends Persistent[String] with User.Existing

object User {

  type NewUser = User with New
  type UpdatedUser = User with Updated
  type ExistingUser = User with Existing
  type UserId = Id[String]

  def apply(name: Name, email: Email, password: Password) = new User(Uuid(), name, email, password, Disable, new Date(), None) with New

  sealed trait Existing extends models.Existing[User, (Option[Name], Option[Email], Option[Password], Option[Activation])] {
    this: User =>

    override def update(updateWith: (Option[Name], Option[Email], Option[Password], Option[Activation])) = {
      val (name, email, password, activation) = updateWith
      new User(this.id, name.getOrElse(this.name),
        email.getOrElse(this.email),
        password.getOrElse(this.password),
        activation.getOrElse(this.active),
        this.creationDate, Some(new Date())) with Updated
    }
  }

}

case class Email(value: String) extends AnyVal

case class Password(value: String) extends AnyVal

case class Name(value: String) extends AnyVal
