package dev.nigredo.protocol

import dev.nigredo.dao.query.Pagination
import dev.nigredo.domain.models.User.UserId
import dev.nigredo.dto.User.{CreateUserDto, UpdateUserDto}
import dev.nigredo.projection.User

object UserProtocol {

  sealed trait UserProtocol extends ApplicationProtocol

  object Query {

    case class UserListRequest(pagination: Pagination) extends UserProtocol

    case class UserListResponse(data: Vector[User]) extends UserProtocol

    case class UserDetailsRequest(id: String) extends UserProtocol

    case class UserDetailsResponse(data: Option[User]) extends UserProtocol

  }

  object Command {

    case class CreateUser(data: CreateUserDto) extends UserProtocol

    case class UpdateUser(id: UserId, data: UpdateUserDto) extends UserProtocol

    case class Created(id: UserId) extends UserProtocol

    case class Updated(id: UserId) extends UserProtocol

    case class DeleteUser(id: UserId) extends UserProtocol

  }

}
