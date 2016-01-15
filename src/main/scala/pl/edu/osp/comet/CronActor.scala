package pl.edu.osp.comet

import net.liftweb.actor.{LAPinger, LiftActor}
import pl.edu.osp.model.{MessagePupil, User, Message}
import net.liftweb.json.JsonDSL._
import java.util.{GregorianCalendar, Calendar}
import net.liftweb.util.Mailer
import net.liftweb.util.Mailer.{PlainMailBodyType, To, Subject, From}


class CronActor extends LiftActor {

  case class Check()

  LAPinger.schedule(this, Check, 30000L)

  override protected def messageHandler = {
    case Check => {
      //sendRepleacmentInfo()
      if (isTime(getHour)) {
        println("run CRON JOB!!!!!!!!!!")
        sendMessages
        sendRepleacmentInfo()
      }
      LAPinger.schedule(this, Check, 1200000L)
    }
  }

  //informacja o wiadomościach indywidualnych, ogłoszeniach nauczycielskich, uwagach i ogłoszeniach dla ucznia

  def sendMessages() {
    println("Uruchomiono sendMessages")

    var userInfo: scala.collection.mutable.Map[Long, Int] = scala.collection.mutable.Map()
    var anouncesCount = 0
    var noticesInfo: scala.collection.mutable.Map[Long, Int] = scala.collection.mutable.Map()
    var anouncesClass: scala.collection.mutable.Map[Long, Int] = scala.collection.mutable.Map()
    User.findAll.map(u => {
      userInfo += (u.id.get -> 0)
    })

    //messages and teacher anounces
    {
      val messages = Message.findAll("mailed" -> false)
      messages.map(mess => {
        if (mess.all) anouncesCount += 1
        else mess.who.map(id => {
          if (userInfo.isDefinedAt(id)) {
            userInfo(id) += 1
          }
        })
        Message.update("_id" -> mess._id.toString, ("$set" -> ("mailed" -> true)))
      })
    }
    //pupil notices and anounces
    {
      MessagePupil.findAll("mailed" -> false).map(mp => {
        if (mp.opinion) {
          if (noticesInfo.isDefinedAt(mp.pupilId)) noticesInfo(mp.pupilId) += 1
          else noticesInfo += (mp.pupilId -> 1)
        } else {
          if (anouncesClass.isDefinedAt(mp.classId)) anouncesClass(mp.classId) += 1
          else anouncesClass += (mp.classId -> 1)
        }

        MessagePupil.update("_id" -> mp._id.toString, ("$set" -> ("mailed" -> true)))
      })
    }




    //wysyłanie informacji do użytkowników
    val textInfoAnounce = "Ogłoszenia, lub komentarze: %d  \n"
    val textInfoMess = "Wiadomości indywidualne: %d \n"
    val textInfoNotices = "Uwagi lub informacje: %d \n"
    val textInfoClassAnoun = "Ogłoszenia: %d \n"
    User.findAll.map(u => {
      var have = false
      val body = if (u.role.get == "r" || u.role.get == "u") {
        (if (noticesInfo.isDefinedAt(u.id.get) && noticesInfo(u.id.get) > 0) {
          have = true; textInfoNotices.format(noticesInfo(u.id.get))
        } else "") +
          (if (userInfo.isDefinedAt(u.id.get) && userInfo(u.id.get) > 0) {
            have = true;
            textInfoMess.format(userInfo(u.id.get))
          } else "") +
          (if (anouncesClass.isDefinedAt(u.classId.get)) {
            have = true; textInfoClassAnoun.format(anouncesClass(u.classId.get))
          }) +
          "Możesz przeczytać informacje na stronie http://edu.epodrecznik.edu.pl/view/index \n "

      } else {
        (if (anouncesCount > 0) {
          have = true; textInfoAnounce.format(anouncesCount)
        } else "") +
          (if (userInfo.isDefinedAt(u.id.get) && userInfo(u.id.get) > 0) {
            have = true; textInfoMess.format(userInfo(u.id.get))
          } else "") +
          "Możesz przeczytać informacje na stronie http://edu.epodrecznik.edu.pl/documents/index \n "
      }

      if (have) Mailer.sendMail(From("automat@xxlo.pl"), Subject("Ogłoszenia i wiadomości na stronie"),
        To(u.email.get), PlainMailBodyType("Na szkolnej stronie internetowej pojawiły się nowe: \n" + body +
          "Informacja wysłana automatycznie, nie odpowiadaj na nią."))
    })
  }


  def sendRepleacmentInfo() {

    Replecements.check()

  }

  def getHour = (new GregorianCalendar()).get(Calendar.HOUR_OF_DAY)

  def isTime(hour: Int) = hour match {
    case 8 => true
    case 12 => true
    case 16 => true
    case 20 => true
    case _ => false
  }

}

