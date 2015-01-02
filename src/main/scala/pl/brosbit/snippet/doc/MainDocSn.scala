package pl.brosbit.snippet.doc

import net.liftweb.http.{S, SHtml}
import net.liftweb.util.Helpers._
import pl.brosbit.model._
import  pl.brosbit.lib.Formater
import net.liftweb.json.JsonDSL._
import scala.xml.{Unparsed, Text}
import net.liftweb.mapper._
import net.liftweb.http.js.JE.{JsFunc, JsRaw}
import net.liftweb.common.Full
import net.liftweb.mapper.ByList
import net.liftweb.http.js.JsCmd
import net.liftweb.json.JsonAST.JValue
import java.util.Date

class MainDocSn extends BaseDoc {

  def showMessages() = {
   val page = S.param("p").openOr("1").toInt
   val perPage = 30
   val q1:JValue = "all"-> true
   val q2:JValue = "who"->("$in"->List(user.id.is))
   val allMess =  Message.findAll(("$or"->List(q1,q2)),("lastDate" -> -1))
   val pages = allMess.length

   val mess = if(page*perPage >= pages) allMess.slice((page-1)*perPage, (page)*perPage)
    else allMess.take(perPage)

    if(mess.isEmpty) ".msg-blue" #> <h2>Brak wiadomości</h2>
    else {
      ".msg-grp" #> mess.map(m => {
        println("showMessage message m.id " + m._id.toString)
        ".msg-grp [class]" #> (if(m.all) "msg-grp msg-blue"  else  "msg-grp msg-green") &
        ".msg" #> m.body.map(b => {
          println("showMessage message body " + b.body + " name:" + b.author)
            ".msg-cont *" #> Unparsed(b.body) &
            ".msg-name *" #> Text(b.author) &
            ".msg-date *" #> Text(b.date)
        }) &
        ".btn-success [onclick]" #> "infoTeacher.openAddComment(this, '%s')".format(m._id.toString)
    })
  }
  }


  def newMessage() = {
    var teacher = ""
    var classId = ""
    var peopleStr = ""
    var body = ""

    def add() {
      val toSend = peopleStr.split(';')
      println("newMessage people to Send: " + peopleStr)
      val allReg = "Ogłoszenie".r
      val mess = Message.create
      val date = new Date
      mess.body = List(MessageChunk(user.id.is, user.getFullName, Formater.formatDate(date), body))
      mess.lastDate = date.getTime
      if(toSend.length > 0 && !toSend.head.trim.isEmpty){
        allReg.findFirstIn(toSend.head) match {
          case Some(str) => {
            println("newMessage people str of Ogłoszenie: " + str)
            mess.all = true
            mess.save
          }
          case _ => {
            val num = """[(\d+)]""".r
            toSend.foreach(adr => {
              num.findFirstIn(adr) match {
                case Some(str) => {
                  println("newMessage people str of Ogłoszenie: " + str)
                  val who = tryo(str.toLong).getOrElse(0L)
                  if(who != 0L) mess.who = who::mess.who
                }
                case _ =>
              }
            })
            mess.who = mess.who.distinct
            println("newMessage people who length: " + mess.who.length)
            if(mess.who.length > 0) mess.save
          }
        }
      }
    }

    val classHead = if(mapClasses.length > 0) mapClasses.head._2 else ""
    val teacherHead = if(mapTeachers.length > 0) mapTeachers.head._2 else ""

    "#classMessage" #> SHtml.select(mapClasses, Full(classHead), classId = _ ) &
    "#teacherMessage" #> SHtml.select(mapTeachers, Full(teacherHead), teacher = _) &
    "#toWhoMessage" #> SHtml.text(peopleStr, peopleStr = _) &
    "#writeMessage" #> SHtml.textarea(body, body = _) &
    "#sendMessage" #> SHtml.button(<span class="glyphicon glyphicon-send"></span> ++ Text(" Wyślij"), add)
    
  }

  def addComment() = {
    var idMessage = ""
    var body = ""
    def add():JsCmd = {
      val date = new Date
      val messChunk =  MessageChunk(user.id.is, user.getFullName, Formater.formatDate(date), body.trim)
      //Message.update("_id"->idMessage.trim, ("lastDate"->("$set"->date.getTime))~("body"->messChunk))
      JsFunc("infoTeacher.insertComment").cmd //poprawić
    }

    val form = "#idMessage" #> SHtml.text(idMessage, idMessage = _) &
    "#writeComment" #> SHtml.textarea(body, body = _) &
    "#addComment" #> SHtml.ajaxSubmit("Dodaj", add) andThen SHtml.makeFormsAjax

    "form" #> (in => form(in))
  }



  def pupilsData() = {
    var pupilsList = ""
    def refresh(classId:String) {
      val pupils = User.findAll(By(User.classId, classId.toLong), By(User.role, "u"))

        pupilsList = "[" + pupils.map(p => {
        val father = p.father.obj.getOrElse(User.create)
        val mather = p.mather.obj.getOrElse(User.create)
          "[%s, '%s', %s, '%s', %s, '%s']".format(p.id.is.toString, p.getFullName,
            father.id.is.toString, father.getFullName, mather.id.is.toString, mather.getFullName)
    }).mkString(",") + "]"
      JsRaw("infoTeacher.refreshPupils()")
    }
    println("PUPILSDATA FUNCTION: " + pupilsList)

    "input" #> SHtml.ajaxText(pupilsList, classId => refresh(classId))
  }


  private def getTeachers = {
    val seq: Seq[String] = List("n", "a", "d", "s")
    User.findAll(ByList(User.role, seq ))
  }


  private lazy val mapTeachers = getTeachers.map(t => (t.id.is.toString, t.shortInfo))
  private def getClasses = ClassModel.findAll(By(ClassModel.scratched, false))
  private lazy val mapClasses = getClasses.map(c => (c.id.is.toString, c.classString()))
  private def getPupilsAndParents = User.findAll(ByList(User.role, List("u", "r")))
}