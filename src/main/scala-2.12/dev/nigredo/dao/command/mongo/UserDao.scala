package dev.nigredo.dao.command.mongo

import java.util.Date

import dev.nigredo.domain.models
import dev.nigredo.domain.models.User.{ExistingUser, NewUser, UpdatedUser}
import dev.nigredo.domain.models._
import reactivemongo.bson.{BSONDateTime, BSONDocument, BSONDocumentReader, BSONInteger, BSONString}

private[dao] object UserDao {

  implicit object DomainUserReader extends BSONDocumentReader[ExistingUser] {
    override def read(doc: BSONDocument): ExistingUser =
      models.User(doc.getAs[BSONString]("id").map(x => Uuid(x.value)).get,
        doc.getAs[BSONString]("name").map(x => Name(x.value)).get,
        doc.getAs[BSONString]("email").map(x => Email(x.value)).get,
        doc.getAs[BSONString]("password").map(x => Password(x.value)).get,
        doc.getAs[BSONInteger]("active").map(x => Activation(x.value)).get,
        doc.getAs[BSONDateTime]("creationDate").map(x => new Date(x.value)).get,
        doc.getAs[BSONDateTime]("modificationDate").map(x => new Date(x.value)))
  }

  def toNewDocument(user: NewUser) = BSONDocument(
    "id" -> user.id.value,
    "name" -> user.name.value,
    "email" -> user.email.value,
    "password" -> user.password.value,
    "active" -> user.active.value,
    "creationDate" -> user.creationDate,
    "modificationDate" -> user.modificationDate
  )

  def toUpdatedDocument(user: UpdatedUser) = BSONDocument("$set" ->
    BSONDocument(
      "name" -> user.name.value,
      "password" -> user.password.value,
      "email" -> user.email.value,
      "active" -> user.active.value,
      "creationDate" -> user.creationDate,
      "modificationDate" -> user.modificationDate
    )
  )

  def emailFilter(email: Email) = BSONDocument("email" -> email.value)
}
