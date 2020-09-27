import akka.actor.ActorSystem
import akka.http.scaladsl._
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import com.typesafe.scalalogging.StrictLogging
import monix.eval.Task
import monix.execution.Scheduler
import org.slf4j.MDC

import scala.compat.java8.OptionConverters._
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.{Random, Try}

object AkkaHTTPExample extends App with StrictLogging {
  MonixMDCAdapter.initialize()
  implicit val actorSystem: ActorSystem = ActorSystem()
  implicit val scheduler: Scheduler     = Scheduler.traced

  val addCorrelationIdHeader: Directive0 =
    mapInnerRoute(_.andThen(_.flatMap {
      case result @ RouteResult.Complete(response) =>
        Future {
          val l = MDC.get("correlationId")
          if (l != null)
            RouteResult.Complete(response.addHeader(RawHeader("correlationId", l)))
          else
            result
        }
      case result @ RouteResult.Rejected(_) =>
        Future {
          result
        }
    }))

  def onCompleteTask[T](task: => Task[T]): Directive1[Try[T]] =
    Directive { inner => ctx =>
      task.runToFuture.transformWith(t => inner(Tuple1(t))(ctx))
    }

  def routes: Route =
    path("hello" / IntNumber) { id =>
      get {
        addCorrelationIdHeader {
          onCompleteTask(Task {
            MDC.put("correlationId", id.toString)
          }) { _ =>
            complete(StatusCodes.OK)
          }
        }
      }
    }

  val serverBinding = Await.result(Http(actorSystem).bindAndHandle(routes, "localhost", 8080), 1.minutes)

  val http = Http()

  val futures = List.fill(512) {
    val int = Random.nextInt(1000)
    for {
      result <- http.singleRequest(
                 HttpRequest(
                   HttpMethods.GET,
                   Uri(s"http://localhost:8080/hello/$int")
                 ))
      header = result.getHeader("correlationId").asScala
      output = s"input $int: output: ${header.map(_.value()).getOrElse("no header")}"
      _      = result.discardEntityBytes()
      _      = println(output)
    } yield ()
  }

  Await.result(Future.sequence(futures), 1.minutes)
  Await.result(serverBinding.terminate(1.minutes), 1.minutes)

  System.exit(0)
}
