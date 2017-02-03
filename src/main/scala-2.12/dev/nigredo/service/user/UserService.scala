package dev.nigredo.service.user

import dev.nigredo.Error.ItemNotFound
import dev.nigredo.Result
import dev.nigredo.domain.models.User.{ExistingUser, NewUser, UpdatedUser, UserId}
import dev.nigredo.dto.User.{CreateUserDto, UpdateUserDto}
import dev.nigredo.service.Service.{Create, Update}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scalaz.Scalaz._
import scalaz.{EitherT, OptionT}

private[service] object UserService {

  def create(transform: CreateUserDto => NewUser)
            (validate: NewUser => Future[Result[NewUser]])
            (persist: NewUser => Future[NewUser]): Create = (dto: CreateUserDto) =>
    (for {
      user <- EitherT.eitherT(validate(transform(dto)))
      result <- EitherT.eitherT(persist(user).map(x => x.right))
    } yield result).run

  def update(load: UserId => Future[Option[ExistingUser]])
            (transform: ExistingUser => UpdateUserDto => UpdatedUser)
            (validate: UpdatedUser => Future[Result[UpdatedUser]])
            (persist: UpdatedUser => Future[UpdatedUser]): Update = (id: UserId) => (dto: UpdateUserDto) =>
    OptionT(load(id)).flatMapF { user =>
      (for {
        user <- EitherT.eitherT(validate(transform(user)(dto)))
        result <- EitherT.eitherT(persist(user).map(x => x.right))
      } yield result).run
    }.getOrElse(ItemNotFound().left)
}
