package dev.nigredo.dao.query.user

import dev.nigredo.dao.query.Pagination
import dev.nigredo.domain.models.User.UserId
import dev.nigredo.projection.User
import reactivemongo.api.Cursor
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson.{BSONDocument, BSONDocumentReader, Macros}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

private[dao] object Mongo {

  implicit val UserReader: BSONDocumentReader[User] = Macros.reader[User]

  def details(collection: Future[BSONCollection]) = (id: UserId) =>
    collection.flatMap(_.find(BSONDocument("id" -> id.value)).one[User])

  def list(collection: Future[BSONCollection]) = (pagination: Pagination) =>
    collection.flatMap(_.find(BSONDocument.empty).cursor()
      .collect[List](pagination.perPage, Cursor.FailOnError[List[User]]()))
}
