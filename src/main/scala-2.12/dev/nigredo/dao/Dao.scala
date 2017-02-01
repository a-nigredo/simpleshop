package dev.nigredo.dao

import dev.nigredo.dao.query.user.DaoActor
import dev.nigredo.{system, _}
import reactivemongo.api.{MongoConnection, MongoDriver}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Dao {

  object Mongo {

    import dev.nigredo.dao.command.user.Mongo._
    import dev.nigredo.dao.query.user.Mongo._

    private lazy val driver = MongoDriver()
    private lazy val parsedUri = MongoConnection.parseURI(dataSourceConfig.getString("mongo.url"))
    private lazy val connection = Future.fromTry(parsedUri.map(driver.connection)).flatMap(_.database(dataSourceConfig.getString("mongo.db")))
    private lazy val userCollection = connection.map(_.collection("user"))

    def queryUserDao = system.actorOf(DaoActor.props(list(userCollection), details(userCollection)))

    def createUser = create(userCollection) _

    def updateUser = update(userCollection) _

    def findUserById = findById(userCollection) _
  }

}
