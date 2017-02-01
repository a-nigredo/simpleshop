package dev.nigredo.domain

package object models {

  sealed trait State

  trait New extends State

  trait Existing[A, B] extends State {
    def update(updateWith: B): A with Updated
  }

  trait Updated extends State

}