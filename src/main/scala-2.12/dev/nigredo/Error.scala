package dev.nigredo

/**
  * Module describes all possible errors in the application
  *
  * @author Andrey Ivanov
  */
object Error {

  sealed trait Error

  case class ValidationError(msgs: List[String]) extends Error

  case class ItemNotFound() extends Error

  case class InternalError(msg: String) extends Error

}
