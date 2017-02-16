package dev.nigredo.service

import dev.nigredo.Types.Command.Create
import dev.nigredo.Types.FResult
import dev.nigredo.domain.models.AccessToken.{NewToken, TokenId, UpdatedToken}
import dev.nigredo.domain.models.Credentials

object Types {

  type CreateToken = Create[Credentials, NewToken]
  type UpdateToken = TokenId => FResult[UpdatedToken]

}
