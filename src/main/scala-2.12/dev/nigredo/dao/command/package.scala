package dev.nigredo.dao

import dev.nigredo.domain.models.{New, Persistent, Updated}

package object command {

  type Create[A <: Persistent[String] with New] = A => A
  type Update[A <: Persistent[String] with Updated] = A => A
}
