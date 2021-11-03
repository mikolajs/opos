package eu.brosbit.opos.snippet.register

import java.util.Date

import eu.brosbit.opos.lib.Formater
import eu.brosbit.opos.model.edu.SubjectTeach
import eu.brosbit.opos.model.LessonThemes
import net.liftweb.http.js.JE.JsFunc
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JsCmds.Alert
import net.liftweb.http.{S, SHtml}
import net.liftweb.util.Helpers._
import net.liftweb.json.JsonDSL._
import net.liftweb.util.CssSel

import scala.xml.NodeSeq

class ThemesSn extends BaseTeacher {
  private val classId = ClassChoose.is
  private val idSub = S.param("idS").map(id => tryo(id.toInt).getOrElse(-1)).openOr(-1)
  private val subjects = SubjectTeach.findAll(("authorId" -> user.id.get))
  private val subject = subjects.find(s => idSub == s.id) match {
    case Some(sub) => sub
    case _ => subjects.headOption.getOrElse(SubjectTeach.create)
  }

  def showThemes():CssSel = {
    val themes = LessonThemes
      .findAll(("classId" -> classId)~("subjectId"->subject.id) ~("teacherId" -> user.id.get ))
      .sortWith((lt1, lt2) => lt1.date > lt2.date)
    "tr" #> themes.map(t => {
      ".themeId *" #> t._id.toString &
      ".theme *" #> t.theme &
      ".lessonDate *" #> Formater.formatDate(new Date(t.date)) &
      ".directing *" #> t.directing &
      ".description *" #> t.description
    })

  }

  def showSubjects():CssSel = {
    "button" #> subjects.map(s => {
      val setClass = if (subject.id == s.id) "btn btn-info" else "btn btn-default"
      <a href={"/register/themes/" + s.id.toString} class ={setClass}>{s.name}</a>
    })
  }

  def chosenSubject():CssSel = {
    "span *" #> subject.name
  }
  def chosenClass():CssSel = {"#chosenClass *" #> ClassString.get}

  def editAjax() = {
    var id = ""
    var theme = ""
    var directing = ""
    var description = ""
    var lessonDate = ""

    def save(): JsCmd = {
      LessonThemes.find(id) match {
        case Some(lessonTheme) => {
          if(lessonTheme.teacherId == user.id.get){
            lessonTheme.theme = theme
            lessonTheme.description = description
            lessonTheme.directing = directing
            lessonTheme.classId = classId
            lessonTheme.subjectId =subject.id
            lessonTheme.date = Formater.fromStringToDate(lessonDate).getTime
            lessonTheme.save
            JsFunc("editThemes.editRow", id).cmd
          } else Alert("Nie jesteś właścieleme tego tematu")

        }
        case _ => {
          val lessonTheme = LessonThemes.create
          lessonTheme.theme = theme
          lessonTheme.description = description
          lessonTheme.directing = directing
          lessonTheme.classId = classId
          lessonTheme.subjectId = subject.id
          lessonTheme.teacherId = user.id.get
          lessonTheme.teacherName = user.getFullName
          lessonTheme.date = Formater.fromStringToDate(lessonDate).getTime
          lessonTheme.save
          JsFunc("editThemes.addRow", id).cmd
        }
      }
    }

    def delete(): JsCmd = {
      LessonThemes.find(id) match {
        case Some(theme) => {
          if (theme.teacherId == user.id.get) {
            theme.delete
            JsFunc("editThemes.deleteRow").cmd
          } else Alert("Nie jesteś właścicielem tematu")
        }
        case _ => Alert("Nie można znaleźć tematu")
      }
    }

    val form = "#themeId" #> SHtml.text(id, id = _) &
      "#theme" #> SHtml.text(theme, x => theme = x.trim()) &
      "#lessonDate" #> SHtml.text(lessonDate, x => lessonDate = x.trim) &
      "#directing" #> SHtml.textarea(directing, x => directing = x.trim) &
      "#description" #> SHtml.textarea(description, x => description = x.trim) &
      "#delete" #> SHtml.ajaxSubmit("Usuń", delete) &
      "#save" #> SHtml.ajaxSubmit("Zapisz", save,
        "onclick" -> "return editThemes.validateForm();") andThen SHtml.makeFormsAjax

    "form" #> ((in:NodeSeq) => form(in))
   }

  def showInfo():CssSel = {
    "#infoClass *" #> ClassString.get &
      "#infoSubject *" #> subject.name
  }

}
