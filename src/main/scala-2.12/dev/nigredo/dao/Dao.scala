package dev.nigredo.dao

import dev.nigredo.domain.models.{Email, Id}
import dev.nigredo.domain.models.User.{ExistingUser, NewUser, UpdatedUser}
import dev.nigredo.projection.User
import dev.nigredo.{system, _}
import reactivemongo.api.{MongoConnection, MongoDriver}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Dao {

  object Mongo {

    import dev.nigredo.dao.command.mongo.UserDao.{DomainUserReader, _}
    import dev.nigredo.dao.command.mongo._
    import dev.nigredo.dao.query.mongo.UserDao.ProjectionUserReader
    import dev.nigredo.dao.query.mongo._

    private lazy val driver = MongoDriver()
    private lazy val parsedUri = MongoConnection.parseURI(dataSourceConfig.getString("mongo.url"))
    private lazy val connection = Future.fromTry(parsedUri.map(driver.connection)).flatMap(_.database(dataSourceConfig.getString("mongo.db")))
    private lazy val userCollection = connection.map(_.collection("user"))

    def findUser = system.actorOf(DaoActor.props(list[User](userCollection), details[User](userCollection)))

    def saveUser = create[NewUser](userCollection)(toNewDocument) _

    def updateUser = update[UpdatedUser](userCollection)(toUpdatedDocument) _

    def findUserById = findById[ExistingUser](userCollection) _

    def isEmailExists(email: Email) = isExists[ExistingUser](userCollection)(emailFilter(email))

    def deleteUser = (id: Id[String]) => delete(userCollection)(idFilter(id))
  }

}
