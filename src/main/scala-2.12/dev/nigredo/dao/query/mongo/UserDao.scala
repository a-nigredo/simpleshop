package dev.nigredo.dao.query.mongo

import akka.actor.{Actor, Props}
import akka.pattern.pipe
import dev.nigredo.Types.Query._
import dev.nigredo.domain.models.User.UserId
import dev.nigredo.domain.models.Uuid
import dev.nigredo.projection.User
import dev.nigredo.protocol.UserProtocol.Query.{UserDetailsRequest, UserDetailsResponse, UserListRequest, UserListResponse}
import reactivemongo.bson.{BSONDocumentReader, Macros}

import scala.concurrent.ExecutionContext.Implicits.global

private[dao] object UserDao {

  implicit val ProjectionUserReader: BSONDocumentReader[User] = Macros.reader[User]

}

private[dao] class DaoActor(list: Items[User], details: Item[UserId, User]) extends Actor {
  override def receive: Receive = {
    case UserListRequest(pagination) => list(pagination).map(UserListResponse).pipeTo(sender)
    case UserDetailsRequest(id) => details(Uuid(id)).map(UserDetailsResponse).pipeTo(sender)
  }
}

private[dao] object DaoActor {
  def props(list: Items[User], details: Item[UserId, User]) = Props(new DaoActor(list, details))
}