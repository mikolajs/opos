package eu.brosbit

import io.vertx.core.Vertx

object Main {
    def main(args:Array[String]):Unit = {
        hello
        Vertx.vertx().createHttpServer().requestHandler(req =>  {
            req.response()
              .putHeader("content-type", "application/json")
              .putHeader("Access-Control-Allow-Origin", "*")
              .end("""{"dane":"hello"}""")
        }).listen(8008, "localhost").onSuccess(server => {
            println("Server: " + server.actualPort())})
    }

    def hello: Unit = {
        println("Hello world!")
        println(msg)
    }
    def msg = "I was compiled by Scala 3. :)"

}

