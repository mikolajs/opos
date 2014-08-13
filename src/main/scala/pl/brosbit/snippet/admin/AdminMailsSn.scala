package pl.brosbit.snippet.admin

import java.util.Date
import scala.xml.{ NodeSeq, Text, XML, Unparsed }
import _root_.net.liftweb.util._
import _root_.pl.brosbit.lib.MailConfig
import _root_.net.liftweb.common._
import _root_.pl.brosbit.model.page._
import _root_.pl.brosbit.model._
import _root_.net.liftweb.http.{ S, SHtml, RequestVar }
import _root_.net.liftweb.mapper.{ Ascending, OrderBy, By }
import _root_.net.liftweb.http.js._
import JsCmds._
import JE._
import Helpers._
import org.bson.types.ObjectId
import _root_.net.liftweb.json.JsonDSL._

class AdminMailsSn {
	object notice extends RequestVar[String]("")
	
   def addContactMail() = {
    var id = ""
    var email = ""
    var description = ""
      
    def saveMail() {
         ContactMail.find(id) match {
        	case Some(contactMail) => {
        	    contactMail.description = description
        	    contactMail.mailAddress = email
        	    contactMail.save
        	}
        	case _ => {
        	   val contactMail = ContactMail.create
        	   contactMail.description = description
              contactMail.mailAddress = email
              contactMail.save
        	}
      }
    }

    def deleteMail() {
        ContactMail.find(id) match {
        	case Some(contactMail) => contactMail.delete
        	case _ =>
      }
    }

    "#id" #> SHtml.text(id, x => id = x, "size" -> "12", "style" -> "display:none;", "id" -> "id") &
      "#descript" #> SHtml.text(description, x => description = x.trim, "maxlength" -> "40", "id" -> "descript") &
      "#mail" #> SHtml.text(email, x => email = x.trim, "maxlength" -> "60", "id" -> "mail") &
      "#save" #> SHtml.submit("Zapisz!", saveMail, "onclick" -> "return validateForm()") &
      "#delete" #> SHtml.submit("Usuń!", deleteMail, "onclick" -> "return confirm('Na pewno chcesz usunąć email?');") &
      "#notice" #> Text(notice)

  }

  /** school contacts mail */
  def contactMails() = {
    val contactMails = ContactMail.findAll
    "tr" #>  contactMails.map(contactMail => {
      <tr ondblclick="setData(this)" id={contactMail._id.toString} >
      <td>{contactMail.mailAddress}</td><td>{contactMail.description}</td></tr>
    })
  }
  
  def addMailConfig() = {
    val mailConfig = new MailConfig
    var (host,user,password) = mailConfig.getConfig
    
    def save(){
      mailConfig.configureMailer(host, user, password)
    }
    
     "#host" #> SHtml.text(host, host = _) &
    "#user" #> SHtml.text(user, user = _) &
    "#password1" #> SHtml.password(password, password = _) &
    "#save" #> SHtml.submit("Zapisz!", save) 
  }
  
}