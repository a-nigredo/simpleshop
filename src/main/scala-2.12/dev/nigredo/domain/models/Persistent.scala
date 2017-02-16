package dev.nigredo.domain.models

import java.util.UUID

trait Persistent[A] {
  val id: A
}

trait Id[A] {
  val value: A
}

case class Uuid(value: String) extends Id[String]

object Uuid {
  def apply() = new Uuid(UUID.randomUUID().toString)
}