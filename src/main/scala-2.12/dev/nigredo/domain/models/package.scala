package dev.nigredo.domain

package object models {

  sealed trait State

  trait New extends State

  trait Existing[A, B] extends State {
    def update(updateWith: B): A with Updated
  }

  trait Updated extends State

  sealed trait Activation {
    val value: Int
  }

  case object Enable extends Activation {
    override val value: Int = 1
  }

  case object Disable extends Activation {
    override val value: Int = 0
  }

  object Activation {
    def apply(code: Int): Activation = code match {
      case 1 => Enable
      case 0 => Disable
    }
  }

}