package dev.nigredo

import dev.nigredo.Error.{InternalError, ItemNotFound, ValidationError}
import dev.nigredo.domain.models._
import dev.nigredo.protocol.ApplicationProtocol
import dev.nigredo.protocol.ApplicationProtocol.{ApplicationProtocol, InvalidData}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scalaz.Scalaz.{Id => _, _}
import scalaz.{-\/, EitherT, OptionT, \/-}

package object service {

  private[service] def create[A, N <: Persistent[String] with New](create: A => N)
                                                                  (validate: N => Future[Result[N]])
                                                                  (persist: N => Future[N]) = (from: A) =>
    (for {
      user <- validate(create(from)).toEitherT
      result <- persist(user).toRight.toEitherT
    } yield result).run

  private[service] def update[A <: Id[String], B, E <: Persistent[String] with Existing, U <: Persistent[String] with Updated](load: A => Future[Option[E]])
                                                                                                                              (update: E => B => U)
                                                                                                                              (validate: E => U => Future[Result[U]])
                                                                                                                              (persist: U => Future[U]) =
    (id: A) => (dto: B) =>
      OptionT(load(id)).flatMapF { user =>
        (for {
          user <- validate(user)(update(user)(dto)).toEitherT
          result <- persist(user).toRight.toEitherT
        } yield result).run
      }.getOrElse(ItemNotFound().left)

  implicit class ToEitherT[A](value: Future[Result[A]]) {
    def toEitherT = EitherT.eitherT(value)
  }

  implicit class Future2Right[A](value: Future[A]) {
    def toRight = value.map(_.right)
  }

  private[service] def respond[A, B <: ApplicationProtocol](response: Result[A], onSuccess: A => B) =
    response match {
      case -\/(err) => err match {
        case err@ValidationError(_) => InvalidData(err)
        case err@InternalError(_) => ApplicationProtocol.InternalError(err)
        case ItemNotFound() => ApplicationProtocol.ItemNotFound
        case _ => ApplicationProtocol.InternalError(InternalError("Something goes wrong"))
      }
      case \/-(user) => onSuccess(user)
    }
}
