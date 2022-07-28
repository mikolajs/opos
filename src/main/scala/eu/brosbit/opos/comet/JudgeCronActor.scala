package eu.brosbit.opos.comet

import eu.brosbit.opos.lib.TestProblemRunner
import net.liftweb.actor.{LAPinger, LiftActor}

import java.util.Date

case class Check()

class JudgeCronActor extends  LiftActor {
  val checkTime = 5000L
  LAPinger.schedule(this, Check, checkTime)

  override protected def messageHandler = {
    case Check => {
        //println("run CRON JOB JUDGE TEST!!!!!!!!!!")
        TestProblemRunner.run()
        LAPinger.schedule(this, Check, checkTime)

    }
  }

}
