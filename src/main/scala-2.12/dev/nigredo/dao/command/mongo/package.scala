package dev.nigredo.dao.command

import dev.nigredo.domain.models.{Id, New, Persistent, Updated}
import reactivemongo.api.BSONSerializationPack.Reader
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson.BSONDocument

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

package object mongo {

  private[dao] def create[A <: Persistent[String] with New](collection: Future[BSONCollection])(document: A => BSONDocument)(entity: A) =
    collection.flatMap(_.insert(document(entity)).map(_ => entity))

  private[dao] def update[A <: Persistent[String] with Updated](collection: Future[BSONCollection])(modifier: A => BSONDocument)(entity: A) =
    collection.flatMap(_.update(BSONDocument("id" -> entity.id.value), modifier(entity)).map(_ => entity))

  private[dao] def findById[A](collection: Future[BSONCollection])(id: Id[String])(implicit reader: Reader[A]) =
    collection.flatMap(_.find(BSONDocument("id" -> id.value)).one[A])
}
