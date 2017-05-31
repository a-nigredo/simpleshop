package dev.nigredo.domain.models

import java.util.Date

import org.scalatest.{FunSuite, Matchers}

class UserTest extends FunSuite with Matchers {

  val password = Password.bcrypt("password")

  test("create new user with default activation") {
    val user = User(Name("user"), Email("test@test.com"), password)

    user.name.value should be("user")
    user.email.value should be("test@test.com")
    user.password.value should be(password.value)
    user.active should be(Disable)
  }

  test("create new user") {
    val user = User(Name("user"), Email("test@test.com"), password, Enable)

    user.name.value should be("user")
    user.email.value should be("test@test.com")
    user.password.value should be(password.value)
    user.active should be(Enable)
  }

  test("create existing user") {
    val id = Uuid()
    val creationDate = new Date()
    val modificationDate = Option(new Date())

    val user = User(id, Name("name"), Email("test@test.com"), password, Enable, creationDate, modificationDate)

    user.id.value should be(id.value)
    user.name.value should be("name")
    user.email.value should be("test@test.com")
    user.password.value should be(password.value)
    user.active should be(Enable)
    user.creationDate should be(creationDate)
    user.modificationDate should be(modificationDate)
  }

  test("update user") {
    val id = Uuid()
    val creationDate = new Date()
    val newPassword = Password.bcrypt("newPassword")
    val user = User(id, Name("name"), Email("test@test.com"), password, Enable, creationDate, None)

    val actual = user.update((Option(Name("newName")), Option(Email("newEmail@test.com")), Option(newPassword), Option(Disable)))

    actual.id.value should be(id.value)
    actual.name.value should be("newName")
    actual.email.value should be("newEmail@test.com")
    actual.password.value should be(newPassword.value)
    actual.active should be(Disable)
    actual.creationDate should be(creationDate)
  }
}
