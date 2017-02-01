package dev.nigredo.dao

import dev.nigredo.domain.models.Id

import scala.concurrent.Future

package object query {

  type Item[A] = Id[String] => Future[Option[A]]
  type Items[A] = Pagination => Future[List[A]]

  case class Pagination(startPage: Int, perPage: Int)

}
