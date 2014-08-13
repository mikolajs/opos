/*
 * Copyright (C) 2011   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register - Wirtualny Dziennik)
 *   LICENCE: GNU AFFERO GENERAL PUBLIC LICENS Version 3 (AGPLv3)
 *   See: <http://www.gnu.org/licenses/>.
 */

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

class AdminEditAdminsSn {

  object notice extends RequestVar[String]("")
  
  def adminsList() = {
    "tbody" #> User.findAll(By(User.role,"a")).map(admin => {
        <tr ondblclick="insertFromTableToForm(this);">
    	<td>{admin.id.toString}</td><td>{admin.firstName}</td>
    	<td>{admin.lastName}</td><td>{admin.email}</td>
    	</tr> 
    })
  }

  def editAdmin() = {
    var id = ""
    var firstName = ""
    var lastName = ""
    var email = ""
    var password1 = ""
    var password2 = ""
    var notice = ""
    def save() {
      val user = User.find(id).openOr(User.create)
      user.firstName(firstName).lastName(lastName).email(email)
      if (password1 != "" && password1 == password2 ) user.password(password1)
      user.role("a").validated(true).save
    }
    def delete() {
      val numberAdmins = User.findAll(By(User.role,"a")).length
      if(numberAdmins > 1) {
        User.find(id) match {
          case Full(user) => user.delete_!
          case _ =>
        }
      }
      else {
        notice = "Nie można usunąć jedynego admistratora"
      }
    }
    "#id" #> SHtml.text(id, id = _) &
    "#firstname" #> SHtml.text(firstName, firstName = _) &
    "#lastname" #> SHtml.text(lastName, lastName = _) &
    "#email" #> SHtml.text(email, email = _) &
    "#password1" #> SHtml.text(password1, x => password1 = x.trim, "type" -> "password") &
      "#password2" #> SHtml.text(password2, x => password2 = x.trim, "type" -> "password") &
      "#save" #> SHtml.submit("Zmień!", save) &
      "#delete" #> SHtml.submit("Usuń!", delete) &
      "#notice" #> Text(notice)
  }

 

  def editSecretariat() = {
    var id = ""
    var firstName = ""
    var lastName = ""
    var email = ""
    def save() {
      val user = User.find(id).openOr(User.create)
      if (id != "0" && id != "") {
        val userChangeList = UserChangeList.create
        userChangeList.firstName(user.firstName.is).lastName(user.lastName.is).email(user.email.is)
          .user(user).date(new Date()).save
      }
      user.firstName(firstName).lastName(lastName).email(email)
      				.role("s").save
    }
    def delete() {
      User.find(id) match {
        case Full(user) => user.validated(false).save
        case _ =>
      }
    }
    "#ID" #> SHtml.text(id, id = _, "readonly" -> "readonly", "id" -> "ID") &
    "#firstName" #> SHtml.text(firstName, firstName = _, "id" -> "firstName", "maxlength" -> "30") &
    "#lastName" #> SHtml.text(lastName, lastName = _, "id" -> "lastName", "maxlength" -> "40") &
    "#email" #> SHtml.text(email, email = _, "id" -> "email", "maxlength" -> "48") &
    "#submit" #> SHtml.submit("Dodaj", save, "onclick" -> "return validate()") &
    "#delete" #> SHtml.submit("Usuń", delete, "onclick" -> "return validID()")
  }

  def showSecretariat() = {
    val secretariatUsers = User.findAll(By(User.role, "s"))
    "tr" #> secretariatUsers.map(user => {
      <tr class={ if (user.validated.is) "normal" else "scratched" } ondblclick="edit(this)" title={
        UserChangeList.findAll(By(UserChangeList.user, user), OrderBy(UserChangeList.date, Ascending)).map(changeList => {
          changeList.date.toString + " " + changeList.lastName.is + " " + changeList.firstName.is + " " +
            changeList.email.is + " " + changeList.passStr
        }).mkString("<br />")
      }>
        <td>{ user.id.is.toString }</td>
        <td>{ user.lastName.is }</td>
        <td>{ user.firstName.is }</td>
        <td>{ user.email.is }</td>
      </tr>
    })
  }


}

