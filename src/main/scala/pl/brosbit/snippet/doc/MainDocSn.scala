package pl.brosbit.snippet.doc

import java.util.Date
import net.liftweb.http.{S, SHtml}
import net.liftweb.util.Helpers._
import pl.brosbit.model.{Announce, Messages, User}
import net.liftweb.json.JsonDSL._
import scala.xml.{Unparsed, Text}
import net.liftweb.mapper._
import pl.brosbit.lib.Formater

class MainDocSn extends BaseDoc {

  def showMessages() = {

    val mess = Messages.findAll("ownerId"->user.id.is).headOption
    if(mess.isEmpty) ".msg" #> ""
    else {

      ".msg" #> mess.get.mes.reverse.map(com => {
        println("MESSAGE: " + com.content)
        ".msg-cont *" #> Unparsed(com.content) &
        ".msg-name *" #> Text(com.authorName) &
        ".msg-name [onclick]" #> "infoTeacher.sendMessage('%s')".format(com.authorId.toString) &
        ".msg-date *" #> Text(com.date) &
        "button [onclick]" #> "infoTeacher.deleteMessage(this, '%s')".format(com.id.toString)
    })
    }
  }

  def delMessage() = {
    S.param("d").openOr("0")
  }

  def showAnnounces() = {
    val anno = Announce.findAll(("pupils"->false), ("_id"->1))
    if(anno.isEmpty) ".msg" #> ""
    else {
      ".msg" #> anno.map(an => {
        println("MESSAGE: " + an.body)
        ".msg-cont *" #> Unparsed(an.body) &
        "h4" #> Text(an.title) &
          ".msg-name *" #> Text(an.authorName) &
          ".msg-name [onclick]" #> "infoTeacher.sendMessage('%s')".format(an.authorId.toString) &
          ".msg-date *" #> Text(Formater.formatTime(new Date(an._id.getTime))) &
          "button [onclick]" #> "infoTeacher.editMessage(this, '%s')".format(an._id.toString)
      })
    }
  }
  
  
  

  def showComments() = {
    "not" #> "empty"
  }

  def editAnnounce() = {
    var idAnno = ""
    var titleAnno = ""
    var bodyAnno = ""
    def edit():Unit = {
      if(! isTeacher ) return
      val anno = Announce.find(idAnno).getOrElse(Announce.create)
      if(anno.authorId != 0L && anno.authorId != user.id.is) return 
      anno.body = bodyAnno
      anno.title = titleAnno
      anno.authorId = user.id.is
      anno.authorName = user.getFullName
      anno.pupils = false;
      anno.save
    }
    
      "#idAnno" #> SHtml.text(idAnno, idAnno = _) &
      "#titleAnno" #> SHtml.text(titleAnno, titleAnno = _) &
      "#bodyAnno" #> SHtml.textarea(bodyAnno, bodyAnno = _) &
      "#editAnno" #> SHtml.button(<span class="glyphicon glyphicon-send"></span> ++ Text("Zapisz"), edit)
  }

  def sendMessage() = {
    var peopleStr = ""
    var body = ""
    def add() {
      
    }
    
    "#toWhoMessage" #> SHtml.text(peopleStr, peopleStr = _) &
    "#bodyMessage" #> SHtml.textarea(body, body = _) &
    "#sendMessage" #> SHtml.button(<span class="glyphicon glyphicon-send"></span> ++ Text("Zapisz"), add)
    
    
  }

  def messageData() = {
    val teachers = User.findAll(By(User.role, "n"), By(User.role, "d"), By(User.role, "s"), By(User.role, "a"))
    val pupils = User.findAll(By(User.role, "u"))
    val teacherData = teachers.map(t => "['" + t.getFullName + "', " + t.id.is.toString + "]").mkString(", ")
    val pupilData = pupils.map(p => {
      val mObj = p.mather.obj
      val mTuple = if (mObj.isEmpty) ("", 0L) else {
        val m = mObj.open_!
        if (m.validated.is && m.email.length() > 4) (m.getFullName, m.id.is)
        else ("", 0L)
      }
      val fObj = p.father.obj
      val fTuple = if (fObj.isEmpty) ("", 0L) else {
        val f = fObj.open_!
        if (f.validated.is && f.email.length() > 4) (f.getFullName, f.id.is)
        else ("", 0L)
      }
      "['%s', %s, 'M: %s', %s, 'T: %s', %s]".format(p.getFullName, p.id.is.toString,
        mTuple._1, mTuple._2.toString, fTuple._1, fTuple._2.toString)
    }).mkString(", ")
    val allData = "var messageData = {'teachers' : [" + teacherData + "]," + " 'pupils' : [" + pupilData + "]}" 
    "script *" #> allData
  }
}
