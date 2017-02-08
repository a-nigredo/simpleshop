package dev.nigredo.domain.validator

import dev.nigredo._
import dev.nigredo.domain.models.{Email, Name, Password, User}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FunSuite, Matchers}

class UserValidatorTest extends FunSuite with ScalaFutures with Matchers {

  test("new user with invalid name, email and email exists") {
    val actual = NewUserValidator(_ => true.fs)(User(Name(""), Email("invalid email"), Password("password"))).futureValue
    actual.leftMap(_.msgs.length shouldBe 3)
  }

  test("new user with invalid name, email and email not exist") {
    val actual = NewUserValidator(_ => false.fs)(User(Name(""), Email("invalid email"), Password("password"))).futureValue
    actual.leftMap(_.msgs.length shouldBe 2)
  }

  test("new user with valid name and email but email exists") {
    val actual = NewUserValidator(_ => true.fs)(User(Name("ValidName"), Email("test@test.com"), Password("password"))).futureValue
    actual.leftMap(_.msgs.length shouldBe 1)
  }

  test("new user with valid name and email") {
    val user = User(Name("ValidName"), Email("test@test.com"), Password("password"))
    val actual = NewUserValidator(_ => false.fs)(user).futureValue
    actual.map(_ shouldBe user)
  }
}
