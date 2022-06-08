package eu.brosbit.opos.comet

import net.liftweb.actor.{LAPinger, LiftActor}

case class Check()

class JudgeCronActor extends  LiftActor {
  val checkTime = 5000L
  LAPinger.schedule(this, Check, checkTime)

  override protected def messageHandler = {
    case Check => {
        println("run CRON JOB JUDGE TEST!!!!!!!!!!")
        LAPinger.schedule(this, Check, checkTime)
    }
  }

}
