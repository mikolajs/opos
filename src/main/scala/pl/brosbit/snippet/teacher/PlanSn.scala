/* Copyright (C) 2011   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register - Wirtualny Dziennik)
 *   LICENCE: GNU AFFERO GENERAL PUBLIC LICENS Version 3 (AGPLv3)
 *   See: <http://www.gnu.org/licenses/>.
 */

package pl.brosbit.snippet.teacher

import _root_.java.util.{ Date, GregorianCalendar, TimeZone }
import _root_.scala.xml.{ NodeSeq, Text, XML }
import scala.collection.mutable.ListBuffer
import _root_.net.liftweb.util._
import _root_.net.liftweb.http.{ SHtml, S }
import _root_.net.liftweb.common._
import Helpers._
import _root_.pl.brosbit.model._
import  _root_.net.liftweb.http.js.JsCmds._
 import  _root_.net.liftweb.http.js.JsCmd
 import  _root_.net.liftweb.http.js.JE._
 import _root_.net.liftweb.json.JsonDSL._

class PlanSn extends BaseTeacher {
  
  val idClass = ClassChoose.is
  val classModel = ClassModel.find(idClass) match {
	  case Full(theClass) => theClass
	  case _ => S.redirectTo("/teacher/index")
  }
  
  val plansOfClass = PlansOfClass.findAll(("classId"->idClass)) match {
       case plan::list => plan
       case _ => {
         val plan = PlansOfClass.create
         plan.classId = classModel.id.is
         plan.classStr = classModel.classString()
         plan.day1 = getEmptyLessonList11
         plan.day2 = getEmptyLessonList11
         plan.day3 = getEmptyLessonList11
         plan.day4 = getEmptyLessonList11
         plan.day5 = getEmptyLessonList11
         plan
       }
   }
  
  def saveData() = {
    var info = "Zapisz zmiany przed opuszczeniem strony!"
    var xmlDataStr = ""
      
     def save(){
       if(isBriningUp){
         var bufferListDay1 = getEmptyListBurrer11
         var bufferListDay2 = getEmptyListBurrer11
         var bufferListDay3 = getEmptyListBurrer11
         var bufferListDay4 = getEmptyListBurrer11
         var bufferListDay5 = getEmptyListBurrer11
         val xmlData = XML.loadString(xmlDataStr)
         (xmlData \ "lesson").foreach(lesson =>{
           val horizontalOrder = tryo((lesson \ "@nr").toString.toInt).openOr(-1)
           val day = horizontalOrder % 5 
           val positionY = horizontalOrder / 5
           val verticalOrder =  horizontalOrder + (day * 11)
           val subject = (lesson \ "@subject").toString
           val info = (lesson \ "@info").toString
           val room = (lesson \ "@room").toString
           val lessonClass = Lesson(subject, info, room)
           (day + 1) match {
             case 1 => bufferListDay1(positionY) = lessonClass
             case 2 => bufferListDay2(positionY) = lessonClass
             case 3 => bufferListDay3(positionY) = lessonClass
             case 4 => bufferListDay4(positionY) = lessonClass
             case 5 => bufferListDay5(positionY) = lessonClass
           }
           
         })
         plansOfClass.day1 = bufferListDay1.toList
         plansOfClass.day2 = bufferListDay2.toList
         plansOfClass.day3 = bufferListDay3.toList
         plansOfClass.day4 = bufferListDay4.toList
         plansOfClass.day5 = bufferListDay5.toList
         plansOfClass.save
         info = "Zapisano zmiany"
       } 
       else {
         info = "Tylko wychowawca może edytować plan!"
       }
     }
    "#infotext *" #> info &
     "#xmlData" #> SHtml.text(xmlDataStr, xmlDataStr = _, "style"->"display:none;") &
     "#save" #> SHtml.submit("ZAPISZ PLAN", save)   
  }
  
   def showTable() = {
       
     
     
     
     val bells = BellsData.getOrCreate
      
     
     "tr" #> (0 to 10).toList.map(i => {
       <tr>
    	 <td>{i.toString}</td>
    	 <td>{"%s - %s".format(bells.beginLesson(i), bells.endLesson(i))}</td>
    	 <td><span class="subject">{plansOfClass.day1(i).subject}</span>
    	 	<span class="info">{plansOfClass.day1(i).info}</span>
    	 	<span class="room">{plansOfClass.day1(i).room}</span>
    	 </td>
    	 <td><span class="subject">{plansOfClass.day2(i).subject}</span>
    	 	<span class="info">{plansOfClass.day2(i).info}</span>
    	 	<span class="room">{plansOfClass.day2(i).room}</span>
    	 </td>
    	 <td><span class="subject">{plansOfClass.day3(i).subject}</span>
    	 	<span class="info">{plansOfClass.day3(i).info}</span>
    	 	<span class="room">{plansOfClass.day3(i).room}</span>
    	 </td>
    	 <td><span class="subject">{plansOfClass.day4(i).subject}</span>
    	 	<span class="info">{plansOfClass.day4(i).info}</span>
    	 	<span class="room">{plansOfClass.day4(i).room}</span>
    	 </td>
    	 <td><span class="subject">{plansOfClass.day5(i).subject}</span>
    	 	<span class="info">{plansOfClass.day5(i).info}</span>
    	 	<span class="room">{plansOfClass.day5(i).room}</span>
    	 </td>
       </tr> 
     }) 
     
   }
   
   def insertSelectSubject = {
     val subjects = SubjectName.findAll.map(subject => subject.name.is)
     "option" #> subjects.map(subject =>  <option value={subject}>{subject}</option>)
   }

	private def getEmptyLessonList11 = {
	  List(Lesson("","",""), Lesson("","",""),Lesson("","",""),Lesson("","",""),
	      Lesson("","",""),Lesson("","",""),Lesson("","",""),Lesson("","",""),
	      Lesson("","",""),Lesson("","",""),Lesson("","",""))
	}
   
	private def getEmptyListBurrer11 = {
	  ListBuffer(Lesson("","",""), Lesson("","",""),Lesson("","",""),Lesson("","",""),
	      Lesson("","",""),Lesson("","",""),Lesson("","",""),Lesson("","",""),
	      Lesson("","",""),Lesson("","",""),Lesson("","",""))
	}
   

}