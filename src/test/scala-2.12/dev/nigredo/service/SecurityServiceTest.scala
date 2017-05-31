package dev.nigredo.service

import java.util.Date

import dev.nigredo.domain.models._
import dev.nigredo.service.SecurityService._
import dev.nigredo.{AuthenticationError, _}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FunSuite, Matchers}

class SecurityServiceTest extends FunSuite with ScalaFutures with Matchers {

  test("login with non existing user") {
    val credentials = Credentials(Email("nonexisting@email.com"), "pass")

    val actual = login(credentials)()(_ => None.fs)(_ => AccessToken(Uuid()).fs)

    actual.futureValue.leftMap(_ shouldBe AuthenticationError)
  }

  test("login with existing user but wrong password") {
    val date = new Date()
    val user = User(Uuid(), Name("name"), Email("test@test.com"), Password.bcrypt("password"), Enable, date, Option(date))
    val credentials = Credentials(Email("nonexisting@email.com"), "pass")

    val actual = login(credentials)()(_ => Option(user).fs)(_ => AccessToken(Uuid()).fs)

    actual.futureValue.leftMap(_ shouldBe AuthenticationError)
  }

  test("login with valid credentials") {
    val date = new Date()
    val user = User(Uuid(), Name("name"), Email("test@test.com"), Password.bcrypt("pass1"), Enable, date, Option(date))
    val at = AccessToken(Uuid())
    val credentials = Credentials(Email("nonexisting@email.com"), "pass")

    val actual = login(credentials)()(_ => Option(user).fs)(_ => at.fs)

    actual.futureValue.map(_ shouldBe at)
  }

  test("prolong token for non existing token") {
    val updatedToken = AccessToken("value", ExpireDate(), Uuid()).update(ExpireDate())
    prolongToken("id")(ExpireDate())(_ => None.fs)(_ => ().fs)(_ => updatedToken.fs).futureValue.leftMap(_ shouldBe AuthenticationError)
  }

  test("prolong expired token") {
    val updatedToken = AccessToken("value", ExpireDate(), Uuid()).update(ExpireDate())

    val actual = prolongToken("id")(ExpireDate())(_ => Option(AccessToken("value", ExpireDate(100), Uuid())).fs)(_ => ().fs)(_ => updatedToken.fs)

    actual.futureValue.leftMap(_ shouldBe AuthenticationError)
  }

  test("prolong token") {
    val updatedToken = AccessToken("value", ExpireDate(), Uuid()).update(ExpireDate())

    val actual = prolongToken("id")(ExpireDate())(_ => Option(AccessToken("value", ExpireDate(), Uuid())).fs)(_ => ().fs)(_ => updatedToken.fs)

    actual.futureValue.map(_ shouldBe updatedToken)
  }
}
