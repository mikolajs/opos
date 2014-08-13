package pl.brosbit.snippet.teacher

import java.util.Date
import scala.xml.Unparsed
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.pl.brosbit.model.page._
import _root_.pl.brosbit.model._
import _root_.net.liftweb.http.{ S, SHtml }
import Helpers._
import org.bson.types.ObjectId
import _root_.net.liftweb.json.JsonDSL._
import  _root_.net.liftweb.http.js.JsCmds._
 import  _root_.net.liftweb.http.js.JsCmd
 import  _root_.net.liftweb.http.js.JE._


class ThemesPlanSn extends BaseTeacher {
	
  def dataTable() = {
    val themesPlans = ThemesPlan.findAll
    "tr" #> themesPlans.map(themesPlan => {
      ".id *" #> themesPlan._id.toString &
      ".classes *" #> themesPlan.classes.mkString(", ") &
      ".subject *" #> themesPlan.subjectStr &
      ".urlLink *" #> Unparsed("<a href=\"" + themesPlan.urlLink + "\">plik</a>") &
      ".teacher *" #> themesPlan.teacherName 
     
    })
  }
  
  def saveAjaxDate() = {
    var id = ""
    var classesTeacher:List[String] = List()
    var subject = ""
    var urlLink = ""
    var errorInfo = ""
    
    def save():JsCmd = {
      val themesPlan = ThemesPlan.find(id).getOrElse(ThemesPlan.create)
      var user = User.currentUser.get
      if(id == "" || user.id.is == themesPlan.teacherId) {
    	  
    	    themesPlan.classes = classesTeacher
            themesPlan.subjectStr = subject
            themesPlan.urlLink = urlLink
            if(id == "") {
              themesPlan.teacherId = user.id.is
              themesPlan.teacherName = user.getFullName
            }
    	  if(themesPlan.isValid){
            themesPlan.save
            JsFunc("editForm.insertRowAndClose", themesPlan._id.toString).cmd
    	  } else Alert("Nie zapisono! Brakuje linku.")
      }
      else Alert("Tylko właściciel może zmienić wpis!")
    }
    
    def delete():JsCmd = {
      if(isAdmin){
        ThemesPlan.find(id) match {
          case Some(themesPlan) => {
            themesPlan.delete
            JsFunc("formEdit.scratchRow", id).cmd
          }
          case _ => Alert("Brak wpisu!")
        }
      } else Alert("Tylko administrator może usunąć wpis!")
    }
    
    val classes = ClassModel.findAll().filter(!_.scratched.is).map(theClass => (theClass.classString(), theClass.classString()))
    val subjects = SubjectName.findAll().filter(!_.scratched.is).map(subject => (subject.name.is, subject.name.is))
    
    val form = "#id" #> SHtml.text(id, id = _, "style"->"display:none;") &
    		"#classes" #> SHtml.multiSelect(classes, classesTeacher, classesTeacher = _) &
    		"#subject" #> SHtml.select(subjects, Full(""), subject = _ ) &
    		"#urlLink" #> SHtml.text(urlLink, urlLink = _) & 
    		"#save" #> SHtml.ajaxSubmit("Zapisz", save, "type"->"image") &
          "#delete" #> {if(isAdmin) SHtml.ajaxSubmit("USUŃ!", delete, "type"->"image") 
        	  					else <span></span>} andThen SHtml.makeFormsAjax
    		
        	 "#loggedTeacher" #> <input id="loggedTeacher" value={User.currentUser.get.getFullName} 
        	 							type="text" style="display:none;"/> & 					
    		 "form" #> (in => form(in))
  }
  
}
