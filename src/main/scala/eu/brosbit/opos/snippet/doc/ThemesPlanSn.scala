package eu.brosbit.opos.snippet.doc


import scala.xml.Unparsed
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.eu.brosbit.opos.model._
import _root_.net.liftweb.http.SHtml
import Helpers._
import _root_.net.liftweb.http.js.JsCmds._
import _root_.net.liftweb.http.js.JsCmd
import _root_.net.liftweb.http.js.JE._


class ThemesPlanSn extends BaseDoc {

  def dataTable() = {
    val themesPlans = ThemesPlan.findAll
    "tr" #> themesPlans.map(themesPlan => {

      ".tdItem" #>
        (<td>{themesPlan._id.toString}</td> ++
        <td>{ themesPlan.classes.mkString(", ") }</td> ++
        <td>{themesPlan.subjectStr} </td> ++
        <td>{Unparsed("<a href=\"" + themesPlan.urlLink + "\">plik</a>")}</td> ++
        <td>{themesPlan.teacherName}</td>)

    })
  }

  def saveAjaxDate() = {
    var id = ""
    var classesTeacher: List[String] = List()
    var subject = ""
    var urlLink = ""
    //var errorInfo = ""

    def save(): JsCmd = {
      val themesPlan = ThemesPlan.find(id).getOrElse(ThemesPlan.create)
      val isNew = themesPlan.urlLink.isEmpty
      val user = User.currentUser.openOrThrowException("No teacher")
      if (id == "" || user.id.get == themesPlan.teacherId) {

        themesPlan.classes = classesTeacher
        themesPlan.subjectStr = subject
        themesPlan.urlLink = urlLink.trim
        if (id == "") {
          themesPlan.teacherId = user.id.get
          themesPlan.teacherName = user.getFullName
        }
        if (themesPlan.isValid) {
          themesPlan.save
          if (isNew) JsFunc("editForm.insertRowAndClear", themesPlan._id.toString).cmd
          else JsFunc("editForm.insertRowAndClose", themesPlan._id.toString).cmd
        } else Alert("Nie zapisono! Brakuje linku.")
      }
      else Alert("Tylko właściciel może zmienić wpis!")
    }

    def delete(): JsCmd = {
      if (isAdmin) {
        ThemesPlan.find(id) match {
          case Some(themesPlan) => {
            themesPlan.delete
            JsFunc("formEdit.deleteRow", id).cmd
          }
          case _ => Alert("Brak wpisu!")
        }
      } else Alert("Tylko administrator może usunąć wpis!")
    }

    val classes = ClassModel.findAll().filter(!_.scratched.get).map(theClass => (theClass.classString(), theClass.classString()))
    val subjects = SubjectName.findAll().filter(!_.scratched.get).map(subject => (subject.name.get, subject.name.get))

    
    val form = "#id" #> SHtml.text(id, id = _, "style"->"display:none;") &
    		"#classes" #> SHtml.multiSelect(classes, classesTeacher, classesTeacher = _) &
    		"#subject" #> SHtml.select(subjects, Full(""), subject = _ ) &
    		"#urlLink" #> SHtml.text(urlLink, urlLink = _) & 
    		"#save" #> SHtml.ajaxSubmit("Zapisz", save, "type"->"image") &
        "#delete" #>  {if(isAdmin) SHtml.ajaxSubmit("USUŃ!", delete, "class"->"btn btn-lg btn-danger")
        else <span style="display:none;" class="brak"></span>} andThen SHtml.makeFormsAjax
    		
        	 "#loggedTeacher" #> <input id="loggedTeacher" value={User.currentUser.openOrThrowException("No teacher").getFullName}
        	 							type="text" style="display:none;"/> & 					
    		 "form" #> (in => form(in))
  }

}
