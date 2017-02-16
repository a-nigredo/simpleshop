package dev.nigredo.service

import dev.nigredo.ItemNotFound
import dev.nigredo.Result
import dev.nigredo.domain.models.{Existing, Id, New, Updated}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scalaz.Scalaz.{Id => _, _}
import scalaz.{EitherT, OptionT}

private[service] object Flow {

  def create[A, N <: New](create: A => N)(validate: N => Future[Result[N]])(persist: N => Future[N]) =
    (from: A) =>
      (for {
        user <- validate(create(from)).toEitherT
        result <- persist(user).toRight.toEitherT
      } yield user).run

  def update[A, B, E <: Existing, U <: Updated](load: A => Future[Option[E]])
                                               (update: E => B => U)
                                               (validate: E => U => Future[Result[U]])
                                               (persist: U => Future[U]) =
    (id: A) => (dto: B) =>
      OptionT(load(id)).flatMapF { user =>
        (for {
          user <- validate(user)(update(user)(dto)).toEitherT
          result <- persist(user).toRight.toEitherT
        } yield user).run
      }.getOrElse(ItemNotFound.left)

  implicit class ToEitherT[A](value: Future[Result[A]]) {
    def toEitherT = EitherT.eitherT(value)
  }

  implicit class Future2Right[A](value: Future[A]) {
    def toRight = value.map(_.right)
  }

}
