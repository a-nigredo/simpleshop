package dev.nigredo.service

import dev.nigredo.Result
import dev.nigredo.domain.models.{Id, New, Persistent, Updated}

import scala.concurrent.Future

object Types {
  type Create[A, B <: Persistent[String] with New] = A => Future[Result[B]]
  type Update[A <: Id[String], B, C <: Persistent[String] with Updated] = A => B => Future[Result[C]]
}
