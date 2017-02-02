package dev.nigredo.service

import java.util.Date

import dev.nigredo.Error.{ItemNotFound, ValidationError}
import dev.nigredo.domain.models._
import dev.nigredo.dto.User.{CreateUserDto, UpdateUserDto}
import org.scalatest.{FunSuite, Matchers}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}

import scala.concurrent.Future

class UserServiceTest extends FunSuite with ScalaFutures with Matchers {

  implicit val defaultPatience = PatienceConfig(Span(1, Seconds), Span(15, Millis))

  import dev.nigredo.service.user.UserService._

  val createdUser = User(Name("name"), Email("email"), Password("password"))
  val existingUser = User(Uuid("test"), Name("name"), Email("email"), Password("password"), Enable, new Date(), None)
  val updatedUser = existingUser.update((None, Some(Email("email")), None, None))
  val successOnCreate = Future.successful(createdUser)
  val successOnUpdate = Future.successful(updatedUser)
  val newUserDto = CreateUserDto("name", "email", "password")
  val updateUserDto = UpdateUserDto(Some("name"), Some("email"), Some("password"), None)
  val error = Left(Future.successful(ValidationError(Nil)))

  test("create new user with invalid data") {
    val actual = create(_ => createdUser)(_ => error)(_ => successOnCreate)
    actual(newUserDto).isLeft shouldBe true
  }

  test("create new user with valid data") {
    val actual = create(_ => createdUser)(_ => Right(createdUser))(_ => successOnCreate)(newUserDto)
    actual.exists(_.futureValue == createdUser) shouldBe true
  }

  test("try update non existing user") {
    val actual = update(_ => Future.successful(None))(_ => _ => updatedUser)(_ => Right(updatedUser))(_ => successOnUpdate)(Uuid())(updateUserDto)
    actual.futureValue.left.map(_.futureValue shouldEqual ItemNotFound())
  }

  test("update existing user with invalid data") {
    val actual = update(_ => Future.successful(Some(existingUser)))(_ => _ => updatedUser)(_ => error)(_ => successOnUpdate)(Uuid())(updateUserDto)
    actual.futureValue.left.map(_.futureValue shouldEqual ValidationError(Nil))
  }

  test("update existing user with valid data") {
    val actual = update(_ => Future.successful(Some(existingUser)))(_ => _ => updatedUser)(_ => Right(updatedUser))(_ => successOnUpdate)(Uuid())(updateUserDto)
    actual.futureValue.right.map(_.futureValue shouldEqual updatedUser)
  }
}
