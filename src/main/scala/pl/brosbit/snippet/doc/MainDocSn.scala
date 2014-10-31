package pl.brosbit.snippet.doc

import net.liftweb.http.S
import net.liftweb.util.Helpers._
import pl.brosbit.model.Messages
import net.liftweb.json.JsonDSL._
import scala.xml.Text

class MainDocSn extends BaseDoc {

  def showMessages() = {

    val mess = Messages.findAll("ownerId"->user.id.is).headOption
    if(mess.isEmpty) ".msg" #> <span></span>
    else {

      ".msg" #> mess.get.mes.reverse.map(com => {
        println("MESSAGE: " + com.content)
        ".msg-cont *" #> Text(com.content) &
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

  def showAnounce() = {

  }

  def addAnounce() = {

  }

  def addMessage() = {

  }
}
