package pl.brosbit.snippet.doc

import net.liftweb.http.{S, SHtml}
import net.liftweb.util.Helpers._
import pl.brosbit.model.{UserMessages, ClassModel, Message, User}
import net.liftweb.json.JsonDSL._
import scala.xml.{Unparsed, Text}
import net.liftweb.mapper._
import net.liftweb.common.Full
import net.liftweb.http.js.JE.JsRaw

class MainDocSn extends BaseDoc {

  val msgType = S.param("s").openOr("a").toLowerCase()
  val msgArch = if(S.param("a").openOr("f") == "f") false else true

  def showMessages() = {

    val mess = if(msgArch) msgType match {
      case "a" => Message.findAll(Nil,("_id" -> -1))
      case "t" => Message.findAll(("dest"->"t"),("_id" -> -1))
      case "i" => Message.findAll(("dest"->"i")~("who"->("$in"->List(user.id.is))),("_id" -> -1))
      case "s" => Message.findAll(("authorId"->user.id.is),("_id" -> -1))
    } else  {
      val uMessT = UserMessages.findAll("userId" -> user.id.is)
      val uMess = if(uMessT.isEmpty) {
        val um = UserMessages.create
        um.userId = user.id.is
        um.userName = user.getFullName
        um
      } else uMessT.head
      val me = (uMess.messLatest:::uMess.messOld).map(ID => ID.toString)
      Message.findAll("_id"->("$in"->me))
    }


    if(mess.isEmpty) ".msg" #> <h2>Brak wiadomości</h2>
    else {

      ".msg" #> mess.map(m => {
        ".msg [class]" #> (m.dest match {
          case "t" => "msg msg-red"
          case "i" => "msg msg-blue"
          case "p" => "msg msg-green"
          case _ => "msg"
        }) &
        ".msg-cont *" #> Unparsed(m.body) &
        ".msg-name *" #> Text(m.authorName) &
        ".msg-date *" #> Text(m.date) &
        ".btn-answer [onclick]" #> "infoTeacher.answerMessage(this, '%s')".format(m.authorId.toString) &
          (if(msgArch) ".close" #> <span></span>
        else ".close [onclick]" #> "infoTeacher.deleteMessage(this, '%s')".format(m._id.toString))
    })
    }
  }
  
  def showSelectors = {
    val sel = "#select" + msgType + " [class]"

    sel #> "list-group-item active"
  }

  def editMessage() = {
    var teacher = ""
    var classId = ""
    var peopleStr = ""
    var body = ""

    def add() {
      
    }

    val classHead = if(mapClasses.length > 0) mapClasses.head._2 else ""
    val teacherHead = if(mapTeachers.length > 0) mapTeachers.head._2 else ""

    "#classMessage" #> SHtml.select(mapClasses, Full(classHead), classId = _ ) &
    "#teacherMessage" #> SHtml.select(mapTeachers, Full(teacherHead), teacher = _) &
    "#toWhoMessage" #> SHtml.text(peopleStr, peopleStr = _) &
    "#bodyMessage" #> SHtml.textarea(body, body = _) &
    "#sendMessage" #> SHtml.button(<span class="glyphicon glyphicon-send"></span> ++ Text(" Wyślij"), add)
    
  }



  def pupilsData() = {
    var pupilsList = ""
    def refresh(classId:String) {
      var pupils = User.findAll(By(User.classId, classId.toLong), By(User.role, "u"))

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


  def deleteMessage() = {
    "input" #> SHtml.ajaxText("", messageId => {
     println("DELETEMESSAGE FUNCION id: " + messageId)
    UserMessages.find("userId"->user.id.is) match {
      case  Some(uMess) => UserMessages.update(("_id"->uMess._id.toString),
        (("$pullAll")->("messLatest"->(messageId)))~(("$pullAll")->("messOld"->(messageId))) ) //sprawdzić czy nie trzeba dodać napis ObjectId("messageId")
      case _ =>
    }
  })
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