/*
 * Copyright (C) 2011   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register - Wirtualny Dziennik)
 *   LICENCE: GNU AFFERO GENERAL PUBLIC LICENS Version 3 (AGPLv3)
 *   See: <http://www.gnu.org/licenses/>.
 *   
 *   Eksportowanie z dziennika do artykułu różnych elementów
 */

package pl.brosbit.snippet.admin

import java.util.Date
import scala.collection.mutable.Map
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.pl.brosbit.model.page._
import _root_.pl.brosbit.model._
import _root_.net.liftweb.http.{ S, SHtml }
import Helpers._
import org.bson.types.ObjectId
import _root_.net.liftweb.json.JsonDSL._
import _root_.net.liftweb.json.JsonAST._

case class SubjectAndClass(var subjects:List[String], var classes:List[String])

class AdminExportPageSn {
 
  var what = ""
    
	def export = {
	   
       var department = ""
       var title = ""
	  
       def save(){
	    val user = User.currentUser.get
	    val articleHead = ArticleHead.create
	    val articleContent = ArticleContent.create
	    articleContent.content = createPageContentSwitcher
	    articleContent.save
	    articleHead.title = title.trim
	    articleHead.authorName = user.getFullName
	    articleHead.authorId = user.id.is
	    if(department.length() == 24) articleHead.departmentId = new org.bson.types.ObjectId(department)
	    articleHead.news = false
	    articleHead.content = articleContent._id
	    articleHead.save
	    S.redirectTo("/index/b?i=" + articleHead._id)
	  }   
         
	  val departments = PageDepartment.findAll.map(department => (department._id.toString, department.name))
	  val actions = List(("p","Plan lekcji"),("e","Zajęcia pozalekcyjne"),("l","Lista nauczycieli"))
	  "#what" #> SHtml.select(actions, Full(actions.head._1) , what = _) &
	  "#department" #> SHtml.select(departments, Full(""), department = _ ) &
	  "#title" #> SHtml.text(title, title = _) &
	  "#save" #> SHtml.submit("Utwórz", save)
	}
	
	
	
	private def createPageContentSwitcher = {
	  what match {
	    case "p" => createPageContentForPlan
	    case "e" => createPageContentForExtraLessons
	    case "l" => createPageContentForTeacherList
	    case _ => "BŁĄD!!!"
	  }
	}
	
	
	private def createPageContentForPlan = {
	  
	  val row = """<tr><td>%d</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>"""
	  val plans = PlansOfClass.findAll(JObject(Nil),("classId"->1))
	  val bells = BellsData.getOrCreate
	  plans.map(plan => {
	    """<div id="%s" class="lessonPlan"><h2>Klasa %s</h2>""".format(plan.classStr, plan.classStr) +
	    """<table><thead><tr><th>Numer</th><th>Godziny</th><th>Poniedziałek</th><th>Wtorek</th>
			  <th>Środa</th><th>Czwartek</th><th>Piątek</th></tr></thead><tbody>""" + 
	    (0 to 10).toList.map(i =>{
	      row.format(i, bells.getLessonTime(i),
	    		  (plan.day1(i).subject + " " + plan.day1(i).info + " " + plan.day1(i).room),
	    		  (plan.day2(i).subject + " " + plan.day2(i).info + " " + plan.day2(i).room),
	    		  (plan.day3(i).subject + " " + plan.day3(i).info + " " + plan.day3(i).room),
	    		  (plan.day4(i).subject + " " + plan.day4(i).info + " " + plan.day4(i).room),
	    		  (plan.day5(i).subject + " " + plan.day5(i).info + " " + plan.day5(i).room))
	    }).mkString +
	     """</tbody></table></div><br/><br/><br/><br/>"""
	  }).mkString
	    
	}
	
	
	
	private def createPageContentForExtraLessons = {
	  
	  val row = """<tr><td>%d</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>"""
	  val extraLessons = ExtraLessons.findAll
	  var i = 0;
	  
	    """<table><thead><tr><th>Lp.</th><th>Temat</th><th>Prowadzący</th><th>Opis</th>
			  <th>Kiedy</th></tr></thead><tbody>""" + 
	      extraLessons.map(extraLesson => {
	        i += 1
	      row.format(i, extraLesson.title, extraLesson.teacherName, extraLesson.description, extraLesson.when)
	    }).mkString +
	     """</tbody></table></div><br/>"""
	    
	}
	
	private def createPageContentForTeacherList = {
	  val row = """<tr><td>%d</td><td>%s</td><td>%s</td><td>%s</td></tr>"""
	  val themesPlans = ThemesPlan.findAll
	  var map:scala.collection.mutable.Map[String,SubjectAndClass] = scala.collection.mutable.Map.empty
	  themesPlans.foreach(themePlan => {
	    if(map.isDefinedAt(themePlan.teacherName)) { 
	      val subAndClass = map(themePlan.teacherName)
	      subAndClass.classes = themePlan.classes ::: subAndClass.classes
	      subAndClass.subjects = themePlan.subjectStr :: subAndClass.subjects
	      map(themePlan.teacherName) = subAndClass
	    }
	    else {
	      map(themePlan.teacherName) = SubjectAndClass(themePlan.subjectStr::Nil, themePlan.classes)
	    }
	  })
	  var i = 0;  
	  val mapKeys = map.keys
	   """<table><thead><tr><th>Lp.</th><th>Imię i Nazwisko</th><th>Nauczane przedmioty</th>
			  <th>Uczy w klasach</th></tr></thead><tbody>""" + 
	      mapKeys.map(key => {
	        i += 1
	        val subAndClass = map(key);
	        val subjects = subAndClass.subjects.distinct.mkString(", ")
	        val classes = subAndClass.classes.distinct.mkString(", ")
	      row.format(i, key, subjects, classes)
	    }).mkString +
	     """</tbody></table></div><br/>"""  
	}
	
	
	
}