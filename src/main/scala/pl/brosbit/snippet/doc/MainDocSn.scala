package pl.brosbit.snippet.doc

import java.util.Date
import net.liftweb.http.S
import net.liftweb.util.Helpers._
import pl.brosbit.model.{Announce, Messages}
import net.liftweb.json.JsonDSL._
import scala.xml.{Unparsed, Text}
import pl.brosbit.lib.Formater

class MainDocSn extends BaseDoc {

  def showMessages() = {

    val mess = Messages.findAll("ownerId"->user.id.is).headOption
    if(mess.isEmpty) ".msg" #> <span></span>
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
    if(anno.isEmpty) ".msg" #> <span></span>
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
    "not" #> "empty"
  }

  def sendMessage() = {
    "not" #> "empty"
  }
}
