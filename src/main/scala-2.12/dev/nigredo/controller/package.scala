package dev.nigredo

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives.{complete, parameters}
import akka.http.scaladsl.server.StandardRoute
import akka.http.scaladsl.server.Directives._

package object controller {

  def withPagination = parameters('page.as[Int] ? config.getInt("pagination.startPage"), 'perPage.as[Int] ? config.getInt("pagination.perPage"))

  object Json {
    def Ok(content: String = ""): StandardRoute = complete(HttpResponse(StatusCodes.OK, entity = HttpEntity(ContentTypes.`application/json`, content)))

    def NotFound = complete(HttpResponse(StatusCodes.NotFound))

    def Unauthorized = complete(HttpResponse(StatusCodes.Unauthorized))

    def Forbidden = complete(HttpResponse(StatusCodes.Forbidden))

    def BadRequest(content: String) = complete(HttpResponse(status = StatusCodes.BadRequest, entity = HttpEntity(ContentTypes.`application/json`, content)))
  }

}
