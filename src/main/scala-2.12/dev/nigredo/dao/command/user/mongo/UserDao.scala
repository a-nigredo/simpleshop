package dev.nigredo.dao.command.user.mongo

import dev.nigredo.domain.models.User.{ExistingUserId, NewUser, UpdatedUser}
import dev.nigredo.domain.models.{Email, Password, User}
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson.{BSONDocument, BSONDocumentWriter}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object UserDao {

  implicit object UserWriter extends BSONDocumentWriter[NewUser] {
    override def write(user: NewUser): BSONDocument =
      BSONDocument("id" -> user.id.value, "name" -> user.name, "email" -> user.email.value, "password" -> user.password.value)
  }

  def create(collection: Future[BSONCollection])(user: NewUser) = collection.map { result =>
    result.insert(user)
    user
  }

  def update(collection: Future[BSONCollection])(id: ExistingUserId)(user: UpdatedUser) = collection.flatMap { result =>
    val selector = BSONDocument("id" -> id.value)
    val modifier = BSONDocument("$set" -> BSONDocument("name" -> user.name, "password" -> user.password.value, "email" -> user.email.value))
    result.update(selector, modifier)
  }

  def findById(id: ExistingUserId)(collection: Future[BSONCollection]) = Future.successful(Some(User("Andrew", Email("asdasd"), Password("ddsfsdf"))))
}
