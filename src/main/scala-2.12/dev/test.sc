import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scalaz.EitherT
import scalaz.Scalaz._

val v1 = Future.successful(None \/> 3)
val v2 = Future.successful(2.right[Int])

val result = (for {
  r1 <- EitherT.eitherT(v1)
  r2 <- EitherT.eitherT(v2)
} yield r1 + r2).toOption

println(Await.result(result.run, Duration.Inf))
