package dev.nigredo.domain.models

import java.util.Date

import dev.nigredo.domain.models
import dev.nigredo.domain.models.User.UserId

case class User private(id: UserId, name: Name, email: Email, password: Password, active: Activation,
                        creationDate: Date, modificationDate: Option[Date]) extends Persistent[UserId]

object User {

  type NewUser = User with New
  type UpdatedUser = User with Updated
  type ExistingUser = User with Existing
  type UserId = Id[String]

  def apply(name: Name, email: Email, password: Password, activation: Activation = Disable) =
    new User(Uuid(), name, email, password, activation, new Date(), Option.empty) with New

  def existing(id: Id[String], name: Name, email: Email, password: Password, active: Activation,
               creationDate: Date, modificationDate: Option[Date]) =
    new User(id, name, email, password, active, creationDate, modificationDate) with Existing

  sealed trait Existing extends models.Existing {
    this: User =>

    override type A = (Option[Name], Option[Email], Option[Password], Option[Activation])
    override type B = User

    override def update(updateWith: A) = {
      val (name, email, password, activation) = updateWith
      new User(this.id, name.getOrElse(this.name), email.getOrElse(this.email), password.getOrElse(this.password),
        activation.getOrElse(this.active), this.creationDate, Option(new Date())) with Updated
    }
  }

}

case class Email(value: String) extends AnyVal

case class Name(value: String) extends AnyVal
