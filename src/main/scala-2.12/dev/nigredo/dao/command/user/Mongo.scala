package dev.nigredo.dao.command.user

import dev.nigredo.domain.models._
import dev.nigredo.domain.models.User.{ExistingUser, NewUser, UpdatedUser, UserId}
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson.{BSONDocument, BSONDocumentReader, BSONString, Macros}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

private[dao] object Mongo {

  implicit object UserReader extends BSONDocumentReader[ExistingUser] {
    override def read(doc: BSONDocument): ExistingUser =
      User(doc.getAs[BSONString]("id").map(x => Uuid(x.value)).get,
        doc.getAs[BSONString]("name").map(x => Name(x.value)).get,
        doc.getAs[BSONString]("email").map(x => Email(x.value)).get,
        doc.getAs[BSONString]("password").map(x => Password(x.value)).get)
  }

  def create(collection: Future[BSONCollection])(user: NewUser) = collection.flatMap { result =>
    val document = BSONDocument(
      "id" -> user.id.value,
      "name" -> user.name.value,
      "email" -> user.email.value,
      "password" -> user.password.value
    )
    result.insert(document).map(_ => user)
  }

  def update(collection: Future[BSONCollection])(user: UpdatedUser) = collection.flatMap { result =>
    val selector = BSONDocument("id" -> user.id.value)
    val modifier = BSONDocument("$set" -> BSONDocument("name" -> user.name.value, "password" -> user.password.value, "email" -> user.email.value))
    result.update(selector, modifier).map(_ => user)
  }

  def findById(collection: Future[BSONCollection])(id: UserId) = collection.flatMap(_.find(BSONDocument("id" -> id.value)).one[ExistingUser])
}
