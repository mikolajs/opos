package eu.brosbit.opos.judge
import eu.brosbit.opos.model.{TestProblem, TestProblemTry}
import net.liftweb.actor.{LAPinger, LAScheduler, LiftActor}

/// WORK ONLY WITH ONE TEST DATA!!!
class TestRunnerActor(parent:LiftActor)  extends  LiftActor {

  override protected def messageHandler = {
    case tpt:TestProblemTry => {
      val testProblem = TestProblem.find(tpt.problem).getOrElse(TestProblem.create)
      val info = if(testProblem.expectedOutputs.isEmpty) {
         CodeOutput(4, "Error Main: no test data to check")
      } else {
        val controlDir = new ControlDir(tpt._id.toString, tpt.code, tpt.lang, testProblem.inputs)
         controlDir.run
      }
      tpt.running = false
      tpt.checked = true
      if(info.code > 1) {
        tpt.outputs = TestValidator.info(info.code)
        tpt.good = false
      } else {
        tpt.good = TestValidator.validate(info.result, testProblem.expectedOutputs)
        tpt.outputs = if(tpt.good) TestValidator.info(0) else TestValidator.info(1)
      }
      tpt.save
    }
    case _ => println("ERROR: Sent not proper data")
  }

}
