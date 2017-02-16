package dev.nigredo

import dev.nigredo.Types.Result
import dev.nigredo.protocol.ApplicationProtocol

import scalaz.{-\/, \/-}

package object actor {

  def respond[A, B <: ApplicationProtocol](response: Result[A])(onSuccess: A => B) =
    response match {
      case -\/(err) => err
      case \/-(user) => onSuccess(user)
    }
}
