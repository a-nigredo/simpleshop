package dev.nigredo.dao.command

import dev.nigredo.domain.models.{Id, New, Persistent, Updated}

import scala.concurrent.Future

object Types {
  type Create[A <: Persistent[String] with New] = A => A
  type Update[A <: Persistent[String] with Updated] = A => A
  type Delete[A] = Id[A] => Future[Unit]
}
