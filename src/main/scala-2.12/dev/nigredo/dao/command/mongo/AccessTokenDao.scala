package dev.nigredo.dao.command.mongo

import dev.nigredo.domain.models.AccessToken.{ExistingAccessToken, NewAccessToken, UpdatedAccessToken}
import dev.nigredo.domain.models.{AccessToken, ExpireDate, Uuid}
import reactivemongo.bson.{BSONDocument, BSONDocumentReader, BSONLong, BSONString}

private[dao] object AccessTokenDao {

  implicit object AccessTokenReader extends BSONDocumentReader[ExistingAccessToken] {
    override def read(doc: BSONDocument): ExistingAccessToken =
      AccessToken.existing(doc.getAs[BSONString]("value").map(_.value).get,
        doc.getAs[BSONLong]("expireDate").map(x => ExpireDate(x.value)).get,
        doc.getAs[BSONString]("user").map(x => Uuid(x.value)).get)
  }

  def toNewDocument(at: NewAccessToken) =
    BSONDocument("value" -> at.value, "expireDate" -> at.expireDate.value, "user" -> at.user.value)

  def toUpdateDocument(at: UpdatedAccessToken) =
    BSONDocument("$set" -> BSONDocument("expireDate" -> at.expireDate.value))

  def valueFilter(value: String) = BSONDocument("value" -> value)
}
