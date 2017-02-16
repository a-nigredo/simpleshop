package dev.nigredo

sealed trait Error

case class ValidationError(msgs: List[String]) extends Error

case object ItemNotFound extends Error

case object AuthenticationError extends Error

case class InternalError(msg: String) extends Error

case object InternalError extends Error
