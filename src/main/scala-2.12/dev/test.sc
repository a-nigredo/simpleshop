import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

val r = for {
  _ <- Future {
    println("Future 1")
    Thread.sleep(8000)
  }
  _ <- Future {
    println("Future 2")
  }
} yield ()

Await.result(r, Duration.Inf)
