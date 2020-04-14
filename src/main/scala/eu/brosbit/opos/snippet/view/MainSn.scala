package eu.brosbit.opos.snippet.view

import scala.xml.{Text, Unparsed}
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import net.liftweb._
import http.{S, SHtml}
import net.liftweb.mapper.{ByList, By}
import eu.brosbit.opos.model._
import Helpers._
import net.liftweb.json.JsonDSL._
import net.liftweb.json.JsonAST.JValue
import eu.brosbit.opos.lib.Formater
import java.util.Date
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JE.JsFunc

class MainSn extends BaseSnippet {

  def showPupilMessages() = {
    val page = S.param("p").openOr("1").toInt
    val perPage = 30
    val q1: JValue = ("opinion" -> false) ~ ("classId" -> user.classId.get)
    val q2: JValue = ("opinion" -> true) ~ ("pupilId" -> user.id.get)
    val allMess = MessagePupil.findAll(("$or" -> List(q1, q2)), ("_id" -> -1))
    println("View pupil messages length: " + allMess.length)
    val pages = allMess.length

    val mess = if (page * perPage >= pages) allMess.slice((page - 1) * perPage, (page) * perPage)
    else allMess.take(perPage)

    if (mess.isEmpty) ".msg-grp" #> <h2>Brak ogłoszeń lub uwag</h2>
    else {
      ".msg-grp" #> mess.map(m => {
        //println("showMessage message m.id " + m._id.toString)
        ".msg-grp [class]" #> (if (m.opinion) "msg-grp msg-red" else "msg-grp msg-blue") &
          ".msg-cont *" #> Unparsed(m.body) &
          ".msg-name *" #> Text(m.teacherName) &
          ".msg-date *" #> Text(m.dateStr)
      })
    }
  }

  def showMessages() = {
    val page = S.param("p").openOr("1").toInt
    val perPage = 30
    val allMess = Message.findAll("who" -> ("$in" -> List(user.id.get)), ("lastDate" -> -1))
    val pages = allMess.length

    val mess = if (page * perPage >= pages) allMess.slice((page - 1) * perPage, (page) * perPage)
    else allMess.take(perPage)

    if (mess.isEmpty) ".msg-grp" #> <h2>Brak wiadomości</h2>
    else {
      ".msg-grp" #> mess.map(m => {
        //println("showMessage message m.id " + m._id.toString)
        ".msg-grp [class]" #> "msg-grp msg-green" &
          ".msg" #> m.body.map(b => {
            //println("showMessage message body " + b.body + " name:" + b.author)
            ".msg-cont *" #> Unparsed(b.body) &
              ".msg-name *" #> Text(b.author) &
              ".msg-date *" #> Text(b.date)
          }) &
          ".btn-success [onclick]" #> "infoPupil.openAddComment(this, '%s')".format(m._id.toString) &
          ".toWhoMessage *" #> m.people
      })
    }
  }

  def newMessage() = {
    var teacherId = ""
    var body = ""

    def add() {
      val mess = Message.create
      val date = new Date
      mess.body = List(MessageChunk(user.id.get.toString, user.getFullName, Formater.formatDate(date), body))
      mess.lastDate = date.getTime
      val who = tryo(teacherId.toLong).getOrElse(0L)
      User.find(By(User.id, who)) match {
        case Full(u) => {
          mess.people = u.getFullName
          mess.who = List(u.id.get, user.id.get)
          mess.people = u.getFullName + " " + user.getFullName
          mess.save
        }
        case _ =>
      }
    }
    val teacherHead = if (mapTeachers.length > 0) mapTeachers.head._2 else ""

    "#teacherMessage" #> SHtml.select(mapTeachers, Full(teacherHead), teacherId = _) &
      "#writeMessage" #> SHtml.textarea(body, body = _) &
      "#sendMessage" #> SHtml.button(Text("Wyślij"), add)

  }

  def addComment() = {
    var idMessage = ""
    var body = ""
    def add(): JsCmd = {
      val date = new Date
      val formatedDate = Formater.formatDate(date)
      val messChunk = MessageChunk(user.id.get.toString, user.getFullName, formatedDate, body.trim)

      Message.update(("_id" -> idMessage.trim),
        ("$set" -> (("lastDate" -> date.getTime) ~ ("mailed" -> false))) ~ ("$addToSet" -> (("body" -> messChunk.toMap))))
      JsFunc("infoPupil.insertComment", user.getFullName + ";" + formatedDate).cmd
    }

    val form = "#idMessage" #> SHtml.text(idMessage, idMessage = _) &
      "#writeComment" #> SHtml.textarea(body, body = _) &
      "#addComment" #> SHtml.ajaxSubmit("Dodaj", add) andThen SHtml.makeFormsAjax

    "form" #> (in => form(in))
  }


  private def getTeachers = {
    val seq: Seq[String] = List("n", "a", "d", "s")
    User.findAll(ByList(User.role, seq))
  }

  private lazy val mapTeachers = getTeachers.map(t => (t.id.get.toString, t.shortInfo))

}