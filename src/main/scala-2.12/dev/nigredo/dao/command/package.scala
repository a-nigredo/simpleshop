package dev.nigredo.dao

import dev.nigredo.domain.models.{Id, New, Persistent, Updated}

import scala.concurrent.Future

package object command {

  type Create[A <: Persistent[String] with New] = A => A
  type Update[A <: Persistent[String] with Updated] = A => A
  type Delete[A] = Id[A] => Future[Unit]
}
