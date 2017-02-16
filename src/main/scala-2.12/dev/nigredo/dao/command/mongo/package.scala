package dev.nigredo.dao.command

import dev.nigredo.domain.models.{Id, New, Persistent, Updated}
import reactivemongo.api.BSONSerializationPack.Reader
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson.BSONDocument

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

package object mongo {

  private[dao] def create[A <: New](collection: Future[BSONCollection])(document: A => BSONDocument)(entity: A) =
    collection.flatMap(_.insert(document(entity)).map(_ => entity))

  private[dao] def update[A <: Persistent[B] with Updated, B](collection: Future[BSONCollection])(modifier: A => BSONDocument)(id: A => BSONDocument)(entity: A) =
    collection.flatMap(_.update(id(entity), modifier(entity)).map(_ => entity))

  private[dao] def findOneByFilter[A](collection: Future[BSONCollection])(filter: BSONDocument)(implicit reader: Reader[A]) =
    collection.flatMap(_.find(filter).one[A])

  private[dao] def isExists[A](collection: Future[BSONCollection])(filter: BSONDocument)(implicit reader: Reader[A]) =
    collection.flatMap(_.find(filter).one[A]).map(_.isDefined)

  private[dao] def delete(collection: Future[BSONCollection])(filter: BSONDocument) =
    collection.flatMap(_.remove(filter).map(_ => ()))
}
