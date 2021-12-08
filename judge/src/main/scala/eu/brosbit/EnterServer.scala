package eu.brosbit

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.*
import akka.http.scaladsl.server.Directives.*
import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

class EnterServer {
  val system = ActorSystem(Behaviors.empty, "http-system")
  val executionContext = system.executionContext
  given ActorSystem[Any] = system
  given ExecutionContext = executionContext

  val route =
    get {
      concat(
        pathSingleSlash {
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<html><body>Hello world!</body></html>"))
        },
        path("ping") {
          complete("PONG!")
        },
        path("crash") {
          complete("BOOM!")
        }
      )
    }


  //val bindingFuture = Http().newServerAt("localhost", 8001).bind(route)
  Http().bindAndHandle(route, "localhost", 8002).onComplete {
    case Success(value) => println(s"Got the callback, value = $value")
    case Failure(e) => e.printStackTrace }

  println(s"HTTP on localhost:8002 has been started")

  def close(): Unit ={
    println("Start close")
    //bindingFuture.flatMap(_.unbind()).onComplete(_ => system.terminate())
  }
}
