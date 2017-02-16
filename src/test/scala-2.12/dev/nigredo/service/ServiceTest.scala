package dev.nigredo.service

import dev.nigredo.{ItemNotFound, ValidationError}
import dev.nigredo._
import dev.nigredo.domain.models._
import dev.nigredo.service.Flow._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.{FunSuite, Matchers}

import scala.concurrent.ExecutionContext.Implicits.global
import scalaz.{-\/, \/-}

class ServiceTest extends FunSuite with ScalaFutures with Matchers {

  implicit val defaultPatience = PatienceConfig(Span(1, Seconds), Span(15, Millis))

  val newEntity = NewEntity(StrId())
  val existing = ExistingEntity(StrId())
  val updated = existing.update(StrId("2"))
  val successOnCreate = newEntity.fs
  val successOnUpdate = updated.fs
  val error = -\/(ValidationError(Nil)).fs
  val id = StrId()
  val dto = Dto("str")

  test("create new entity with invalid data") {
    create[Dto, NewEntity](_ => newEntity)(_ => error)(_ => successOnCreate)(dto).map(_.isLeft shouldBe true)
  }

  test("create new entity with valid data") {
    create[Dto, NewEntity](_ => newEntity)(_ => \/-(newEntity).fs)(_ => successOnCreate)(dto).futureValue.exists(_ == newEntity) shouldBe true
  }

  test("try update non existing entity") {
    update[StrId, Dto, ExistingEntity, UpdateEntity](_ => Option.empty.fs)(_ => _ => updated)(_ => _ => \/-(updated).fs)(_ => successOnUpdate)(id)(dto).futureValue.map(_ shouldEqual ItemNotFound)
  }

  test("update existing user with invalid data") {
    update[StrId, Dto, ExistingEntity, UpdateEntity](_ => Option(existing).fs)(_ => _ => updated)(_ => _ => error)(_ => successOnUpdate)(id)(dto).futureValue.map(_ shouldEqual ValidationError(Nil))
  }

  test("update existing user with valid data") {
    update[StrId, Dto, ExistingEntity, UpdateEntity](_ => Option(existing).fs)(_ => _ => updated)(_ => _ => \/-(updated).fs)(_ => successOnUpdate)(id)(dto).futureValue.map(_ shouldEqual updated)
  }

  case class Dto(value: String)

  case class StrId(value: String = "1") extends Id[String]

  case class NewEntity(id: StrId) extends Persistent[Id[String]] with New

  case class ExistingEntity(id: StrId) extends Persistent[Id[String]] with Existing {
    override type A = StrId
    override type B = UpdateEntity

    override def update(updateWith: StrId): UpdateEntity with Updated = UpdateEntity(updateWith)
  }

  case class UpdateEntity(id: StrId) extends Persistent[Id[String]] with Updated

}
