package eu.brosbit

import akka.actor.typed.ActorSystem
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.AbstractBehavior
import akka.actor.typed.scaladsl.ActorContext
import akka.actor.typed.scaladsl.Behaviors
import scala.io.StdIn


object ActorStarter extends App:
  val testSystem = ActorSystem(MainActor.get, "startActor")
  testSystem ! "start"
  val enterServer = EnterServer()
  println("Before readln")
  //enterServer.bindingFuture
  StdIn.readLine()
  enterServer.close()
  println("End readln")


class MainActor(context: ActorContext[String]) extends AbstractBehavior[String](context):
  override def onMessage(msg:String):Behavior[String] =
    msg match
      case "start" =>
        println("started")
        this

object  MainActor:
  def get: Behavior[String] = Behaviors.setup(context => MainActor(context))

