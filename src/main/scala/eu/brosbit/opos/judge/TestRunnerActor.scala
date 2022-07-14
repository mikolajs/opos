package eu.brosbit.opos.judge
import eu.brosbit.opos.model.{TestProblem, TestProblemTry}
import net.liftweb.actor.{LAPinger, LAScheduler, LiftActor}

/// WORK ONLY WITH ONE TEST DATA!!!
class TestRunnerActor(parent:LiftActor)  extends  LiftActor {

  override protected def messageHandler = {
    case tpt:TestProblemTry => {
      val testProblem = TestProblem.find(tpt.problem).getOrElse(TestProblem.create)
      var info = ""
      if(testProblem.expectedOutputs.isEmpty) {
        info = "Error Main: no test data to check"
      } else {
        val controlDir = new ControlDir(tpt._id.toString, tpt.code, tpt.lang, testProblem.inputs, testProblem.expectedOutputs)
        info = controlDir.run
      }
      tpt.running = false
      tpt.outputs = List(info)
      if(tpt.outputs.head.take(5) != "Error") tpt.good = List(true) else tpt.good = List(false)
      tpt.save
    }
    case _ => println("ERROR: Sent not proper data")
  }

}
