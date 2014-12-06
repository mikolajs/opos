package pl.brosbit.snippet.doc

import java.util.Date
import net.liftweb.http.{S, SHtml}
import net.liftweb.util.Helpers._
import pl.brosbit.model.{ Message, User}
import net.liftweb.json.JsonDSL._
import scala.xml.{Unparsed, Text}
import net.liftweb.mapper._
import pl.brosbit.lib.Formater

class MainDocSn extends BaseDoc {

  val selector = S.param("s").openOr("a").toLowerCase()

  def showMessages() = {

    val mess = selector match {
      case "a" => Message.findAll(("authorId"->user.id.is),("_id" -> -1))
      case letter => Message.findAll(("authorId"->user.id.is)~("dest"->letter),("_id" -> -1))
    }


    if(mess.isEmpty) ".msg" #> <h2>Brak wiadomości</h2>
    else {

      ".msg" #> mess.map(m => {
        ".msg [class]" #> (m.dest match {
          case "c" => "msg msg-orange"
          case "t" => "msg msg-red"
          case "i" => "msg msg-blue"
          case "p" => "msg msg-green"
          case _ => "msg"
        }) &
        ".msg-cont *" #> Unparsed(m.body) &
        ".msg-name *" #> Text(m.authorName) &
        ".msg-name [onclick]" #> "infoTeacher.sendMessage('%s [%s]')".format(m.authorName, m.authorId.toString) &
        ".msg-date *" #> Text(m.date) &
        "button [onclick]" #> "infoTeacher.editMessage(this, '%s')".format(m._id.toString)
    })
    }
  }
  
  def showSelectors = {
    val sel = "#select" + selector + " [class]"

    sel #> "list-group-item active"
  }

  def editMessage() = {
    var peopleStr = ""
    var body = ""
    def add() {
      
    }
    
    "#toWhoMessage" #> SHtml.text(peopleStr, peopleStr = _) &
    "#bodyMessage" #> SHtml.textarea(body, body = _) &
    "#sendMessage" #> SHtml.button(<span class="glyphicon glyphicon-send"></span> ++ Text(" Wyślij"), add)
    
    
  }

  def newMessage() =  {
    var peopleStr = ""
    var body = ""
    def add() {

    }

    "#toWhoMessage" #> SHtml.text(peopleStr, peopleStr = _) &
      "#bodyMessage" #> SHtml.textarea(body, body = _) &
      "#sendMessage" #> SHtml.button(<span class="glyphicon glyphicon-send"></span> ++ Text(" Wyślij"), add)


  }

  def messageData() = {
    val teachers = User.findAll(By(User.role, "n"), By(User.role, "d"), By(User.role, "s"), By(User.role, "a"))
    val pupils = User.findAll(By(User.role, "u"))
    val teacherData = teachers.map(t => "['" + t.getFullName + "', " + t.id.is.toString + "]").mkString(", ")
    val pupilData = pupils.map(p => {
      val mObj = p.mather.obj
      val mTuple = if (mObj.isEmpty) ("", 0L) else {
        val m = mObj.openOrThrowException("open empty parent")
        if (m.validated.is && m.email.is.length() > 4) (m.getFullName, m.id.is)
        else ("", 0L)
      }
      val fObj = p.father.obj
      val fTuple = if (fObj.isEmpty) ("", 0L) else {
        val f = fObj.openOrThrowException("open empty parent")
        if (f.validated.is && f.email.is.length() > 4) (f.getFullName, f.id.is)
        else ("", 0L)
      }
      "['%s', %s, 'M: %s', %s, 'T: %s', %s]".format(p.getFullName, p.id.is.toString,
        mTuple._1, mTuple._2.toString, fTuple._1, fTuple._2.toString)
    }).mkString(", ")
    val allData = "var messageData = {'teachers' : [" + teacherData + "]," + " 'pupils' : [" + pupilData + "]}" 
    "script *" #> allData
  }
}
