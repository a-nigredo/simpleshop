package dev.nigredo.domain.models

import java.security.MessageDigest._
import java.time.Instant
import java.util.UUID

import dev.nigredo._
import dev.nigredo.domain.models
import dev.nigredo.domain.models.AccessToken.TokenId
import dev.nigredo.domain.models.User.UserId

import scala.concurrent.duration.Duration

case class AccessToken private(value: String, expireDate: ExpireDate, user: UserId) extends Persistent[TokenId] {
  def isExpired = Instant.now().toEpochMilli > expireDate.value

  override val id = value
}

object AccessToken {

  type NewToken = AccessToken with New
  type ExistingToken = AccessToken with Existing
  type UpdatedToken = AccessToken with Updated
  type TokenId = String

  def apply(user: UserId, durationLifeTime: Duration = Duration(config.getString("security.tokenLifeTime"))) = {

    def md5Hash(text: String) = getInstance("MD5").digest(text.getBytes()).map(0xFF & _).map("%02x".format(_)).foldLeft("")(_ + _)

    new AccessToken(md5Hash(UUID.randomUUID().toString), ExpireDate(durationLifeTime), user) with New
  }

  def existing(value: String, expireDate: ExpireDate, user: UserId) = new AccessToken(value, expireDate, user) with Existing

  sealed trait Existing extends models.Existing {
    this: AccessToken =>

    override type A = ExpireDate
    override type B = AccessToken

    override def update(updateWith: A) = new AccessToken(this.value, updateWith, this.user) with Updated
  }

}

case class ExpireDate(value: Long) extends AnyVal

object ExpireDate {
  def apply(durationLifeTime: Duration = Duration(config.getString("security.tokenLifeTime"))) =
    new ExpireDate(Instant.now().plusMillis(durationLifeTime.toMillis).toEpochMilli)
}

case class Credentials(email: Email, password: String)