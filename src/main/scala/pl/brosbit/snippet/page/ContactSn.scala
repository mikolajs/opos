/*
 * Copyright (C) 2011   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register - Wirtualny Dziennik)
 *   LICENCE: GNU AFFERO GENERAL PUBLIC LICENS Version 3 (AGPLv3)
 *   See: <http://www.gnu.org/licenses/>.
 */

package pl.brosbit.snippet.page

import _root_.scala.xml.{ NodeSeq }
import _root_.net.liftweb.util._
import Mailer._
import _root_.net.liftweb.common._
import _root_.pl.brosbit.model.page._
import _root_. pl.brosbit.model._
import _root_.pl.brosbit.lib._
import _root_.net.liftweb.http.{ SHtml, S }
import Helpers._

class ContactSn {

  val contactMails = ContactMail.findAll.map(contactMail =>
    (contactMail.description -> contactMail.description))

  def getData() = {
    val contactMails = ContactMail.findAll
    val contactMailsToForm = contactMails.map(contactMail =>
    (contactMail.description -> contactMail.description))
    var theme = ""
    var content = ""
    var mail = ""
    var selectedMail = ""

    def sendMail() {
      val emailList = contactMails.filter(contactMail => {
        contactMail.description == selectedMail
      })
      if(!emailList.isEmpty) {
    	 val emailToSend = emailList.head.mailAddress
         val body = content + "\n" + "----------\n Informacja wysłana ze strony przez: " + mail
         Mailer.sendMail(From("zestrony@zkpig26.gda.pl"), Subject(theme), 
          To(emailToSend), PlainMailBodyType(body))
          S.redirectTo("/contact")
      } 
      else S.notice("Błędny email")
     
    }

    "#theme" #> SHtml.text(theme, x => theme = x) &
      "#mailcontent" #> SHtml.textarea(content, x => content = x) &
      "#mail" #> SHtml.text(mail, x => mail = x) &
      "#select" #> SHtml.select(contactMailsToForm, Empty, x => selectedMail = x) &
      "#submit" #> SHtml.submit("Wyślij!", sendMail, "onclick" -> "return isValid();")

  }
}

 