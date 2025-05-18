package eu.brosbit.opos.snippet.edu


import scala.xml.{Text}
import _root_.net.liftweb._
import http.{S, SHtml}
import common._
import util._
import eu.brosbit.opos.model._
import edu._
import Helpers._


class EditDocumentSn extends BaseResourceSn {

  val userId = user.id.get

  val id = S.param("id").openOr("0")
  var document = if (id != "0") Document.find(id).getOrElse(Document.create) else Document.create

  val isOwner = document.authorId == 0L || document.authorId == userId


  def editData() = {
    if(!isOwner) S.redirectTo("/educontent/documents")
    var docID = id
    var title = document.title
    var descript = document.descript
    val subject = if (document.subjectId == 0L) subjectNow.name else document.subjectName
    var level = document.lev.toString
    var department = if(document.department.isEmpty) departName
        else document.department
    var docContent = document.content

    def save() {
      //println("-------------save------------title-----------------")
      if (isOwner && !title.trim.isEmpty) {
        document.title = title
        document.descript = descript
        if (document.subjectId == 0L) document.subjectId = subjectNow.id
        document.lev = level.toInt
        document.subjectName = findSubjectName(document.subjectId)
        document.authorId = user.id.get
        document.authorName = user.getFullName
        document.content = docContent
        document.department = department
        document.save
        if (id == "0") S.redirectTo("/educontent/editdocument/" + document._id.toString)
        //Alert("Zapisano")

      }
    }

    def delete() = {
      if (isOwner) {
        document.delete
      }
    }

    //val subjects = this.subjectTeach.map(sub => (sub.id.toString, sub.name))
    val departs = if (document.subjectId == 0L) subjectNow.departments.map(d => (d, d))
    else subjectTeach.find(s => s.id == document.subjectId).getOrElse(subjectNow)
      .departments.map(d => (d, d))
    "#docID" #> SHtml.text(docID, docID = _) &
      "#docTitle" #> SHtml.text(title, title = _) &
      "#docDescription" #> SHtml.textarea(descript, descript = _) &
      "#subject" #> SHtml.text(subject, x => Unit) &
      "#docLevel" #> SHtml.select(levList, Full(level), level = _) &
      "#department" #> SHtml.select(departs, Full(department), department = _) &
      "#docContent" #> SHtml.textarea(docContent, d => docContent = d.trim) &
      "#docSave" #> SHtml.button(<span class="glyphicon glyphicon-floppy-save"></span>
        ++ Text(" Zapisz"), save, "title" -> "Zapisz") &
      "#docDelete" #> SHtml.button(<span class="glyphicon glyphicon-trash"></span>
        ++ Text(" Usuń "), delete, "title" -> "Usuń")
  }

  def backData():CssSel = {
    "a [href]" #> s"/educontent/documents?s=${subjectNow.id}&d=${departNr}"
  }

  private def findDepart(): Unit ={

  }

}
