package dev.nigredo.dao

import dev.nigredo.domain.models.{Id, Persistent}

package object command {
  type Create[A <: Persistent] = A => A
  type Update[A <: Persistent, B <: Id[String, Id.Existing]] = B => A => A
}
