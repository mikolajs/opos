package eu.brosbit.opos.lib

import eu.brosbit.opos.judge.{CodeToTest, TestRunnerActor}
import eu.brosbit.opos.model.TestProblemTry
import net.liftweb.actor.LiftActor

import scala.collection.mutable
import net.liftweb.json.JsonDSL._

import java.util.Date

object TestProblemRunner {
  val maxTestSimultaneously = 5
  val path = eu.brosbit.opos.lib.ConfigLoader.judgeDir
  val toTestQueue:mutable.Queue[CodeToTest] = mutable.Queue[CodeToTest]()
  val runnerActorTable = Array.ofDim[TestRunnerActor](maxTestSimultaneously)
  var last = maxTestSimultaneously
  def init(mainActor:LiftActor) = {
    for (i <- 0 until runnerActorTable.length) runnerActorTable(i) = new TestRunnerActor(mainActor)
  }
  //it must insert code to directory
  def run() = {
    val testProblemTries = TestProblemTry.findAll("checked" -> false, "running" -> false).sortWith((tpt1, tpt2) => tpt1.aDate < tpt2.aDate)
    testProblemTries.foreach( tpt => {
      tpt.running = true
      tpt.timeRun = new Date().getTime
      tpt.save
      last += 1
      if(last >= maxTestSimultaneously) last = 0
      runnerActorTable(last) ! tpt
    })
  }

  def clearNotEndedProblems(): Unit = {
    val d = new Date().getTime
    val testProblemTries = TestProblemTry.findAll("checked" -> false, "running" -> true)
      .filter(tpt => (d - tpt.timeRun) > 60000L)
  }
}
