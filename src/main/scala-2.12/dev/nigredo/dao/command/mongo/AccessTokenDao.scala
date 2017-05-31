package dev.nigredo.dao.command.mongo

import dev.nigredo.domain.models.AccessToken.{ExistingToken, NewToken, UpdatedToken}
import dev.nigredo.domain.models.{AccessToken, ExpireDate, Uuid}
import reactivemongo.bson.{BSONDocument, BSONDocumentReader, BSONLong, BSONString}

private[dao] object AccessTokenDao {

  implicit object AccessTokenReader extends BSONDocumentReader[ExistingToken] {
    override def read(doc: BSONDocument): ExistingToken =
      AccessToken(doc.getAs[BSONString]("value").map(_.value).get,
        doc.getAs[BSONLong]("expireDate").map(x => ExpireDate(x.value)).get,
        doc.getAs[BSONString]("user").map(x => Uuid(x.value)).get)
  }

  def toNewDocument(at: NewToken) =
    BSONDocument("value" -> at.value, "expireDate" -> at.expireDate.value, "user" -> at.user.value)

  def toUpdateDocument(at: UpdatedToken) =
    BSONDocument("$set" -> BSONDocument("expireDate" -> at.expireDate.value))

  def valueFilter(value: String) = BSONDocument("value" -> value)
}
