package dev.nigredo.domain.models

import java.util.UUID

trait Persistent

trait Id[A, B <: Id.State] extends Persistent {
  val value: A
}

object Id {

  sealed trait State

  sealed trait New extends State

  sealed trait Existing extends State

}

case class Uuid[B <: Id.State] private(value: String) extends Id[String, B]

object Uuid {

  type NewUuid = Uuid[Id.New]
  type ExistingUuId = Uuid[Id.Existing]

  def apply(): NewUuid = new Uuid[Id.New](UUID.randomUUID().toString)

//  def apply(value: String): ExistingUuId = new Uuid[Id.Existing](value)
}