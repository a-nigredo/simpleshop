package dev.nigredo.dao.impl

import dev.nigredo.dao.command
import dev.nigredo.domain.models.AccessToken.{ExistingAccessToken, NewAccessToken, TokenId, UpdatedAccessToken}
import dev.nigredo.domain.models.User.{ExistingUser, NewUser, UpdatedUser, UserId}
import dev.nigredo.domain.models.{Email, Id}
import dev.nigredo.projection.User
import dev.nigredo.{system, _}
import reactivemongo.api.{MongoConnection, MongoDriver}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Mongo {

  import dev.nigredo.dao.command.mongo.AccessTokenDao._
  import dev.nigredo.dao.command.mongo.UserDao.{DomainUserReader, _}
  import dev.nigredo.dao.command.mongo._
  import dev.nigredo.dao.query.mongo.UserDao.ProjectionUserReader
  import dev.nigredo.dao.query.mongo._

  private lazy val driver = MongoDriver()
  private lazy val parsedUri = MongoConnection.parseURI(dataSourceConfig.getString("mongo.url"))
  private lazy val connection = Future.fromTry(parsedUri.map(driver.connection)).flatMap(_.database(dataSourceConfig.getString("mongo.db")))
  private lazy val userCollection = connection.map(_.collection("user"))
  private lazy val tokenCollection = connection.map(_.collection("tokens"))

  def removeToken(value: String) = delete(tokenCollection)(valueFilter(value))

  def addToken = create[NewAccessToken](tokenCollection)(AccessTokenDao.toNewDocument) _

  def saveToken = update[UpdatedAccessToken, TokenId](tokenCollection)(AccessTokenDao.toUpdateDocument)(x => valueFilter(x.value)) _

  def findTokenByValue(value: String) = findOneByFilter[ExistingAccessToken](tokenCollection)(valueFilter(value))

  def findUser = system.actorOf(DaoActor.props(list[User](userCollection), details[User](userCollection)))

  def addUser = create[NewUser](userCollection)(command.mongo.UserDao.toNewDocument) _

  def updateUser = update[UpdatedUser, UserId](userCollection)(command.mongo.UserDao.toUpdatedDocument)(x => idFilter(x.id)) _

  def findUserById(id: UserId) = findOneByFilter[ExistingUser](userCollection)(idFilter(id))

  def findActiveUserByEmail(email: Email) = findOneByFilter[ExistingUser](userCollection)(emailFilter(email) ++ activateFilter())

  def isEmailExists(email: Email) = isExists[ExistingUser](userCollection)(emailFilter(email))

  def deleteUser = (id: Id[String]) => delete(userCollection)(idFilter(id))
}

