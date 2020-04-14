package eu.brosbit.opos.snippet.doc


import scala.xml.Unparsed
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.eu.brosbit.opos.model._
import _root_.net.liftweb.http.SHtml
import Helpers._
import  _root_.net.liftweb.http.js.JsCmds._
import  _root_.net.liftweb.http.js.JsCmd
import  _root_.net.liftweb.http.js.JE._


class PSOSn extends BaseDoc {

  def dataTable() = {
    val psos =  PSO.findAll
    "tr" #> psos.map(p => {
      ".tdItem" #>
        (<td>{p._id.toString}</td> ++
          <td>{ p.classes.mkString(", ") }</td> ++
          <td>{p.subjectStr} </td> ++
          <td>{Unparsed("<a href=\"" + p.urlLink + "\">plik</a>")}</td> ++
          <td>{p.teacherName}</td>)
    })
  }

  def saveAjaxDate() = {
    var id = ""
    var classesTeacher:List[String] = List()
    var subject = ""
    var urlLink = ""
    //var errorInfo = ""

    def save():JsCmd = {
      val pso = PSO.find(id).getOrElse(PSO.create)
      val isNew = pso.urlLink.isEmpty
      val user = User.currentUser.openOrThrowException("No current user")
      if(id == "" || user.id.get == pso.teacherId) {

        pso.classes = classesTeacher
        pso.subjectStr = subject
        pso.urlLink = urlLink
        if(id == "") {
          pso.teacherId = user.id.get
          pso.teacherName = user.getFullName
        }
        if(pso.isValid){
          pso.save
          if(isNew) JsFunc("editForm.insertRowAndClear", pso._id.toString).cmd
          else JsFunc("editForm.insertRowAndClose", pso._id.toString).cmd
        } else Alert("Nie zapisono! Brakuje linku.")
      }
      else Alert("Tylko właściciel może zmienić wpis!")
    }

    def delete():JsCmd = {
      if(isAdmin){
        PSO.find(id) match {
          case Some(pso) => {
            pso.delete
            JsFunc("formEdit.scratchRow", id).cmd
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
      "#save" #> SHtml.ajaxSubmit("Zapisz", save) &
      "#delete" #>  {if(isAdmin) SHtml.ajaxSubmit("USUŃ!", delete, "class"->"btn btn-lg btn-danger")
      else <span style="display:none;" class="brak"></span>} andThen SHtml.makeFormsAjax

    "#loggedTeacher" #> <input id="loggedTeacher" value={User.currentUser.openOrThrowException("No logged user").getFullName}
                               type="text" style="display:none;"/> &
      "form" #> (in => form(in))
  }

}
