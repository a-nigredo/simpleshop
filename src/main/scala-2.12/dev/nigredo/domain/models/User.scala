package dev.nigredo.domain.models

import dev.nigredo.domain.models.User.State
import dev.nigredo.domain.models.User.State.{Existing, New, Updated}

case class User[S <: State, A <: Id.State] private(id: Id[String, A], name: String, email: Email, password: Password)
  extends Persistent

object User {

  type NewUser = User[New, Id.New]
  type UpdatedUser = User[Updated, Id.Existing]
  type ExistingUser = User[Existing, Id.Existing]
  type NewUserId = Id[String, Id.New]
  type ExistingUserId = Id[String, Id.Existing]

  def apply(name: String, email: Email, password: Password) = new User[New, Id.New](Uuid(), name, email, password)

  def apply(id: ExistingUserId, name: String, email: Email, password: Password) = new User[Existing, Id.Existing](id, name, email, password)

  sealed trait State

  object State {

    sealed trait New extends State

    sealed trait Existing extends State

    sealed trait Updated extends Existing

  }

  implicit class Update(existing: ExistingUser) {
    def update(name: String, email: Email, password: Password): User[Updated, Id.Existing] = new User[Updated, Id.Existing](existing.id, name, email, password)
  }

}

case class Email(value: String) extends AnyVal

case class Password(value: String) extends AnyVal
