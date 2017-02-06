package dev.nigredo.service.user

import java.util.Date

import dev.nigredo.Error.{ItemNotFound, ValidationError}
import dev.nigredo.domain.models._
import dev.nigredo.dto.User.{CreateUserDto, UpdateUserDto}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.{FunSuite, Matchers}

import scala.concurrent.ExecutionContext.Implicits.global
import scalaz.{-\/, \/-}

class UserServiceTest extends FunSuite with ScalaFutures with Matchers {

  implicit val defaultPatience = PatienceConfig(Span(1, Seconds), Span(15, Millis))

  import dev.nigredo._
  import dev.nigredo.service.user.UserService._

  val createdUser = User(Name("name"), Email("email"), Password("password"))
  val existingUser = User(Uuid("test"), Name("name"), Email("email"), Password("password"), Enable, new Date(), None)
  val updatedUser = existingUser.update((None, Some(Email("email")), None, None))
  val successOnCreate = createdUser.fs
  val successOnUpdate = updatedUser.fs
  val newUserDto = CreateUserDto("name", "email", "password")
  val updateUserDto = UpdateUserDto(Some("name"), Some("email"), Some("password"), None)
  val error = -\/(ValidationError(Nil)).fs
  val id = Uuid()

  test("create new user with invalid data") {
    val actual = create(_ => createdUser)(_ => error)(_ => successOnCreate)
    actual(newUserDto).map(_.isLeft shouldBe true)
  }

  test("create new user with valid data") {
    val actual = create(_ => createdUser)(_ => \/-(createdUser).fs)(_ => successOnCreate)(newUserDto)
    actual.futureValue.exists(_ == createdUser) shouldBe true
  }

  test("try update non existing user") {
    val actual = update(_ => None.fs)(_ => _ => updatedUser)(_ => \/-(updatedUser).fs)(_ => successOnUpdate)(id)(updateUserDto)
    actual.futureValue.map(_ shouldEqual ItemNotFound())
  }

  test("update existing user with invalid data") {
    val actual = update(_ => Some(existingUser).fs)(_ => _ => updatedUser)(_ => error)(_ => successOnUpdate)(id)(updateUserDto)
    actual.futureValue.map(_ shouldEqual ValidationError(Nil))
  }

  test("update existing user with valid data") {
    val actual = update(_ => Some(existingUser).fs)(_ => _ => updatedUser)(_ => \/-(updatedUser).fs)(_ => successOnUpdate)(id)(updateUserDto)
    actual.futureValue.map(_ shouldEqual updatedUser)
  }
}
