package dev.nigredo.dao.query.user.mongo

import dev.nigredo.dao.query.Pagination
import dev.nigredo.domain.models.User.{ExistingUserId, NewUserId}
import dev.nigredo.projection.User
import reactivemongo.api.Cursor
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson.{BSONDocument, BSONDocumentReader, BSONDouble, BSONString}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

private[dao] object UserDaoMongo {

  implicit object UserBSONReader extends BSONDocumentReader[User] {
    override def read(doc: BSONDocument): User =
      User(doc.getAs[BSONDouble]("id").get.value.toLong,
        doc.getAs[BSONString]("name").get.value,
        doc.getAs[BSONString]("email").get.value,
        doc.getAs[BSONString]("password").get.value)
  }

  def details(collection: Future[BSONCollection]) = (id: ExistingUserId) =>
    collection.flatMap(_.find(BSONDocument("id" -> id.value)).one[User])

  def list(collection: Future[BSONCollection]) = (pagination: Pagination) =>
    collection.flatMap(_.find(BSONDocument.empty).cursor()
      .collect[List](pagination.perPage, Cursor.FailOnError[List[User]]()))
}
