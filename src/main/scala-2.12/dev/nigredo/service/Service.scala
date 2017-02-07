package dev.nigredo.service

import dev.nigredo.Error.ItemNotFound
import dev.nigredo.Result
import dev.nigredo.domain.models.{Persistent, _}
import dev.nigredo.service.user.UserService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scalaz.Scalaz.{Id => _, _}
import scalaz.{EitherT, OptionT}

object Service {

  type Create[A, B <: Persistent[String] with New] = A => Future[Result[B]]
  type Update[A <: Id[String], B, C <: Persistent[String] with Updated] = A => B => Future[Result[C]]

  def userService = UserService()

  import dev.nigredo._

  private[service] def create[A, N <: Persistent[String] with New](create: A => N)
                                                                  (validate: N => Future[Result[N]])
                                                                  (persist: N => Future[N]) = (from: A) =>
    (for {
      user <- validate(create(from)).toEitherT
      result <- persist(user).toRight.toEitherT
    } yield result).run

  private[service] def update[A <: Id[String], B, E <: Persistent[String] with Existing, U <: Persistent[String] with Updated](load: A => Future[Option[E]])
                                                                                                                              (update: E => B => U)
                                                                                                                              (validate: U => Future[Result[U]])
                                                                                                                              (persist: U => Future[U]) =
    (id: A) => (dto: B) =>
      OptionT(load(id)).flatMapF { user =>
        (for {
          user <- validate(update(user)(dto)).toEitherT
          result <- persist(user).toRight.toEitherT
        } yield result).run
      }.getOrElse(ItemNotFound().left)

  implicit class ToEitherT[A](value: Future[Result[A]]) {
    def toEitherT = EitherT.eitherT(value)
  }

  implicit class Future2Right[A](value: Future[A]) {
    def toRight = value.map(_.right)
  }

}
