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
  import pl.brosbit.model._
  import _root_.net.liftweb.mapper.{ By, OrderBy, Ascending }
  import Helpers._
  import  _root_.net.liftweb.http.js.JsCmds._
  import  _root_.net.liftweb.http.js.JsCmd
  import  _root_.net.liftweb.http.js.JE._

    class ClassSn extends {


      def teachers()  = User.findAll(By(User.role, "n")).filter(teacher => !teacher.scratched.is).
    		  map(teacher => {
    		    val teacherString = teacher.shortInfo
    		    (teacherString,teacherString)
    		  })

      def classList() = {

        //var out:NodeSeq = NodeSeq.Empty
        val classList: List[ClassModel] = ClassModel.findAll(OrderBy(ClassModel.level, Ascending))
        //val user:List[User] = User.find(class.id.is).get
        "tr" #> classList.map(theClass => {
          "tr [class]" #> { if (theClass.scratched.is) "scratched" else "" } &
           ".id" #> <td>{ theClass.id.is.toString }</td> &
            ".level" #> <td>{ theClass.level.is.toString }</td> &
            ".division" #> <td>{ theClass.division.is }</td> &
            ".teacher" #> <td>{theClass.teacher.obj match {
              case Full(teacher) => teacher.shortInfo
              case _ => "Brak"
            }}</td> &
            ".description" #> <td>{ theClass.descript.is }</td>
        })
      }

      def editAjax() = {
    var id = ""
    var level = ""
    var division = ""
    var teacher = ""
    var description = ""
    var errorInfo = ""
      
    def save() =  {
      val theClass = ClassModel.find(id).openOr(ClassModel.create)
      val teacherId = refitTeacherIdFromShortInfo(teacher)
      val teacherModel = User.find(teacherId).openOr(User.create)
      if (teacherModel.role == "n") {
        val levelInt = tryo(level.toInt).openOr(0)
        theClass.level(levelInt).descript(description).division(division).
        	teacher(teacherModel.id.is).scratched(false).save
        if(id == "") {
           id = theClass.id.toString
           JsFunc("editForm.insertRowAndClear", id).cmd         
        }
        else {
          id = theClass.id.toString
          JsFunc("editForm.insertRowAndClose", id).cmd 
        }
        	
      }
      else {
        errorInfo = "Nieprawidłowy nauczyciel"
        JsNull.cmd
      }
      
    }
    
    def delete() = {
      ClassModel.find(id) match {
        case Full(theClass) => {
          theClass.scratched(true).save
          JsFunc("editForm.scratchRow", id).cmd
        }
        case _ => Alert("Nie ma takiej klasy")
      }
    }
    
   val teacherPairList = teachers()
   val levels = (0 to 6).toList.map(level => (level.toString,level.toString))

    val form = "#id" #> SHtml.text(id, x => id = x.trim, "readonly"-> "readonly") &
       "#level" #> SHtml.select(levels, Full("0"), level = _) &
        "#division" #> SHtml.text(division, x => division = x.trim) &
       "#teacher" #>   SHtml.select(teacherPairList,Full(""), teacher = _) &
       "#description" #> SHtml.text(description, x => description = x.trim) &
       "#addInfo *" #> errorInfo &
       "#delete" #> SHtml.ajaxSubmit("Usuń", delete, "type"->"image", 
           "onclick" -> "return confirm('Na pewno usunąć klasę?')") &
      "#save" #> SHtml.ajaxSubmit("Zapisz", save, "type"->"image") andThen SHtml.makeFormsAjax

      "form" #> (in => form(in))
   }
   
  
  private def refitTeacherIdFromShortInfo(shortInfo:String) = 
    shortInfo.split('[').last.split(']').head
    
  }
 
