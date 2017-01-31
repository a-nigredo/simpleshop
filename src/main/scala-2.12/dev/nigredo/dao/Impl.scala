package dev.nigredo.dao

import dev.nigredo.dao.query.user.DaoActor
import dev.nigredo.dao.query.user.mongo.UserDaoMongo
import reactivemongo.api.{MongoConnection, MongoDriver}
import dev.nigredo.system
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import dev.nigredo._

object Impl {

  object Mongo {

    private val driver = MongoDriver()
    private val parsedUri = MongoConnection.parseURI(dataSourceConfig.getString("mongo.url"))
    private val connection = Future.fromTry(parsedUri.map(driver.connection)).flatMap(_.database(dataSourceConfig.getString("mongo.db")))
    private val userCollection = connection.map(_.collection("user"))

    def queryUserDao = system.actorOf(DaoActor.props(UserDaoMongo.list(userCollection), UserDaoMongo.details(userCollection)))
  }

}
