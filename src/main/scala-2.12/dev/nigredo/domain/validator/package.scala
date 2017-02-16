package dev.nigredo.domain

import dev.nigredo.ValidationError

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scalaz.{-\/, \/-}

package object validator {

  private[validator] def validate[A](validators: List[A => Future[Option[String]]])(entity: A) = {
    Future.sequence(validators.map(_ (entity))).map { result =>
      result.filter(_.isDefined) match {
        case l@x :: xs => -\/(ValidationError(l.map(_.get)))
        case Nil => \/-(entity)
      }
    }
  }
}
