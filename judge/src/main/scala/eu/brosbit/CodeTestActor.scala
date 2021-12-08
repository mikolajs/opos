package eu.brosbit

import akka.actor.typed.{ActorRef, ActorSystem, Behavior, PostStop, Signal}
import akka.actor.typed.scaladsl.AbstractBehavior
import akka.actor.typed.scaladsl.ActorContext
import akka.actor.typed.scaladsl.Behaviors

case class CodeTestDataInput(id:String, source:String, language:String, testInputData:List[String],
                             responseTo:ActorRef[CodeTestDataOutput])
case class CodeTestDataOutput(id:String, testOutputData:List[String])

class CodeTestActor(user:String, context: ActorContext[CodeTestDataInput]) extends AbstractBehavior[CodeTestDataInput](context){
  val controlDir = ControlDir(user)
  override def onMessage(msg:CodeTestDataInput):Behavior[CodeTestDataInput] =
    msg match
      case msgC =>
        println("Info")
        msgC.responseTo ! CodeTestDataOutput("", Nil) //controlDir.runCpp  ///change to real action
        this

  override def onSignal: PartialFunction[Signal, Behavior[CodeTestDataInput]] =
    case PostStop =>
      println("user " + user + " has stopped")
      this

}
