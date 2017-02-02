package dev.nigredo.dao.query

import dev.nigredo.domain.models.Id
import reactivemongo.api.BSONSerializationPack.Reader
import reactivemongo.api.Cursor
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson.BSONDocument

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

package object mongo {

  private[dao] def details[A](collection: Future[BSONCollection])(implicit reader: Reader[A]) = (id: Id[String]) =>
    collection.flatMap(_.find(BSONDocument("id" -> id.value)).one[A])

  private[dao] def list[A](collection: Future[BSONCollection])(implicit reader: Reader[A]) = (pagination: Pagination) =>
    collection.flatMap(_.find(BSONDocument.empty).cursor()
      .collect[Vector](pagination.perPage, Cursor.FailOnError[Vector[A]]()))
}
