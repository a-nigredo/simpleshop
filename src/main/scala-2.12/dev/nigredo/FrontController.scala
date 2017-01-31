package dev.nigredo

import akka.http.scaladsl.server.Directives.{pathPrefix, _}
import akka.util.Timeout
import dev.nigredo.controller.query.UserController
import dev.nigredo.dao.Impl
import dev.nigredo.dao.command.user.mongo
import dev.nigredo.dao.command.user.mongo.UserDao
import dev.nigredo.domain.models.{Email, Id, Password, User}
import dev.nigredo.domain.models.User.ExistingUser
import dev.nigredo.domain.models.User.State.{Existing, New, Updated}
import dev.nigredo.dto.User.UpdateUserDto
import dev.nigredo.service.{UserService, UserServiceActor}
import dev.nigredo.domain.validator.UserValidator

import scala.concurrent.duration._

object FrontController {

  implicit val timeout = Timeout(5 seconds)

//  val onCreate = UserService.create(dto => User(dto.name, Email(dto.email), Password(dto.password)))(UserValidator.validate[New, Id.New])(UserDao.create)
//  val onUpdate = UserService.update(mongo.UserDao.findById)((x: ExistingUser) => (dto: UpdateUserDto) =>
//    x.update(dto.name.getOrElse(x.name), dto.email.map(Email).getOrElse(x.email), dto.password.map(Password).getOrElse(x.password)))(UserValidator.validate[Updated, Id.Existing])(mongo.UserDao.update)
//  val userService = system.actorOf(UserServiceActor.props(onCreate, onUpdate))

  val routes = ???
//  pathPrefix("api") {
//    pathPrefix("user") {
//      UserController.route(Impl.Mongo.queryUserDao) ~ controller.command.UserController.route(userService)
//    }
//  }
}
