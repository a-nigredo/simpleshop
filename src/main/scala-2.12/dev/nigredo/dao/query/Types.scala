package dev.nigredo.dao.query

import dev.nigredo.domain.models.Id

import scala.concurrent.Future

object Types {
  type Item[A] = Id[String] => Future[Option[A]]
  type Items[A] = Pagination => Future[Vector[A]]
}
