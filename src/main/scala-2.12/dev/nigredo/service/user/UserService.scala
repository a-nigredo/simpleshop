package dev.nigredo.service.user

import dev.nigredo.Error.ItemNotFound
import dev.nigredo.Result
import dev.nigredo.domain.models.User.{ExistingUser, NewUser, UpdatedUser, UserId}
import dev.nigredo.dto.User.{CreateUserDto, UpdateUserDto}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

private[service] object UserService {

  type Create = CreateUserDto => Result[Future[NewUser]]
  type Update = UserId => UpdateUserDto => Future[Result[Future[UpdatedUser]]]

  def create(fromDto: CreateUserDto => NewUser)
            (validate: NewUser => Result[NewUser])
            (persist: NewUser => Future[NewUser]): Create = validate compose fromDto andThen (_.map(persist))

  def update(load: UserId => Future[Option[ExistingUser]])
            (fromDto: ExistingUser => UpdateUserDto => UpdatedUser)
            (validate: UpdatedUser => Result[UpdatedUser])
            (persist: UpdatedUser => Future[UpdatedUser]): Update = (id: UserId) => (dto: UpdateUserDto) =>
    load(id).map(_.map(user => validate(fromDto(user)(dto)).map(persist)).getOrElse(Left(Future.successful(ItemNotFound()))))
}
