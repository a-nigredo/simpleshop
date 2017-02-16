package dev.nigredo

import dev.nigredo.dao.query.Pagination

import scala.concurrent.Future

object Types {

  object Command {
    type Create[A, B] = A => Future[Result[B]]
    type Update[A, B, C] = A => B => Future[Result[C]]
    type Delete[A] = A => Future[Unit]
  }

  object Query {
    type Item[A, B] = A => Future[Option[B]]
    type Items[A] = Pagination => Future[Vector[A]]
  }

}
