/*
 * Copyright (C) 2011   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register - Wirtualny Dziennik)
 *
 *   VRegister is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU AFFERO GENERAL PUBLIC LICENS Version 3
 *   as published by the Free Software Foundation
 *
 *   VRegister is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU AFFERO GENERAL PUBLIC LICENS
 *   along with VRegister.  If not, see <http://www.gnu.org/licenses/>.
 */

package pl.brosbit.snippet.secretariat

    import _root_.scala.xml.{ NodeSeq, Text, XML }
    import _root_.net.liftweb.util._
    import _root_.net.liftweb.http.{ SHtml, S }
    import _root_.net.liftweb.common._
    import _root_.java.util.{ Date, Random, GregorianCalendar, TimeZone }
    import _root_.net.liftweb.mapper.{ By, OrderBy, Ascending }
    import Helpers._
    import _root_.pl.brosbit.model._
    import  _root_.net.liftweb.http.js.JsCmds._
    import  _root_.net.liftweb.http.js.JsCmd
    import  _root_.net.liftweb.http.js.JE._

    class AddTeacherSn {

      def teacherList() = {
        val teachers: List[User] = User.findAll(By(User.role, "n"),OrderBy(User.id, Ascending))

        "tr" #> teachers.map(teacher => {
          "tr [class]" #> { if (teacher.scratched.is) "scratched" else "" } &
            ".id" #> <td>{ teacher.id.is.toString }</td> &
            ".firstname" #> <td>{ teacher.firstName.is }</td> &
            ".lastname" #> <td>{ teacher.lastName.is }</td> &
            ".email" #> <td>{ teacher.email.is }</td>  &
            ".phone" #> <td>{ teacher.phone.is }</td>
        })

      }
      
       def editAjax() = {
    var userId = ""
    var lastName = ""
    var firstName = ""
    var telephone = ""
    var email = ""  
      
    def save() =  {
      val user = User.find(userId).openOr(User.create)
      user.lastName(lastName).firstName(firstName).phone(telephone).email(email).
      role("n").scratched(false).save
      if (userId == ""){
        userId = user.id.toString
        JsFunc("editForm.insertRowAndClear", userId).cmd
      } 
      else {
        userId = user.id.toString
        JsFunc("editForm.insertRowAndClose", userId).cmd
      }
      
    }
    
    def delete() = {
      User.find(userId) match {
        case Full(user) => {
          user.scratched(true).validated(false).save
          JsFunc("editForm.scratchRow", userId).cmd
        }
        case _ => Alert("Nie ma takiego użytkownika")
      }
    }
    
   
   

    val form = "#id" #> SHtml.text(userId, x => userId = x.trim, "readonly"-> "readonly") &
      "#lastname" #> SHtml.text(lastName, x => lastName = x.trim) &
       "#firstname" #> SHtml.text(firstName, x => firstName = x.trim) &
       "#email" #> SHtml.text(email, x => email = x.trim) &
       "#telephone" #> SHtml.text(telephone, x => telephone = x.trim) &
       "#delete" #> SHtml.ajaxSubmit("Usuń", delete, "type"->"image", 
           "onclick" -> "return confirm('Na pewno usunąć użytkownika?')") &
      "#save" #> SHtml.ajaxSubmit("Zapisz", save, "type"->"image",
          "onclick" -> "return editForm.validate();") andThen SHtml.makeFormsAjax

      "form" #> (in => form(in))
       }
    }


