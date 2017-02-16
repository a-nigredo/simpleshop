package dev.nigredo.service

import dev.nigredo.AuthenticationError
import dev.nigredo.domain.models.AccessToken.{ExistingToken, NewToken, TokenId, UpdatedToken}
import dev.nigredo.domain.models.User.ExistingUser
import dev.nigredo.domain.models._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scalaz.Scalaz.{Id => _, _}
import scalaz.{-\/, OptionT, \/-}

object SecurityService {

  def login(credentials: Credentials)
           (findUser: Email => Future[Option[ExistingUser]])
           (onSuccess: NewToken => Future[NewToken]) =
    OptionT(findUser(credentials.email)).flatMapF { user =>
      if (user.password.value == Password.bcrypt(credentials.password, user.password.salt).value)
        onSuccess(AccessToken(user.id)).map(x => \/-(x))
      else -\/(AuthenticationError).fs
    }.getOrElse(-\/(AuthenticationError))

  def prolongToken(tokenValue: TokenId)
                  (expireDate: => ExpireDate)
                  (findToken: TokenId => Future[Option[ExistingToken]])
                  (onExpire: TokenId => Future[Unit])
                  (onUpdate: UpdatedToken => Future[UpdatedToken]) =
    OptionT(findToken(tokenValue)).flatMapF { token =>
      if (token.isExpired) onExpire(tokenValue).map(_ => -\/(AuthenticationError))
      else onUpdate(token.update(expireDate)).map(x => \/-(x))
    }.getOrElse(-\/(AuthenticationError))
}
