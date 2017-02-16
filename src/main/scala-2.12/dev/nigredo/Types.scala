package dev.nigredo

import dev.nigredo.dao.query.Pagination

import scala.concurrent.Future
import scalaz.\/

object Types {

  type Result[A] = \/[Error, A]
  type FResult[A] = Future[Result[A]]

  object Command {
    type Create[A, B] = A => FResult[B]
    type Update[A, B, C] = A => B => FResult[C]
    type Delete[A] = A => Future[Unit]
  }

  object Query {
    type Item[A, B] = A => Future[Option[B]]
    type Items[A] = Pagination => Future[Vector[A]]
  }

}
