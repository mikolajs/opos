package pl.brosbit.snippet.edu

import java.util.Date
import scala.xml.{ Text, XML, Unparsed, Elem, Null, TopScope }
import _root_.net.liftweb._
import http.{ S, SHtml }
import common._
import util._
import mapper.{ OrderBy, Descending }
import pl.brosbit.model._
import edu._
import mapper.By
import json.JsonDSL._
import json.JsonAST.JObject
import json.JsonParser
import org.bson.types.ObjectId
import Helpers._
import http.js.{ JsCmds, JsCmd }
import http.js.JsCmds.{ SetHtml, Alert, Run }
import http.js.JE._

class EditDocumentSn extends BaseResourceSn {

  val userId = user.id.is

  val id = S.param("id").openOr("0")
  var document = if (id != "0") Document.find(id).getOrElse(Document.create) else Document.create

  val isOwner =
    document.authorId == 0L || document.authorId == userId

  def editData() = {

    var docID = id
    var title = document.title
    var descript = document.descript
    var subject = if(document.subjectId == 0L) subjectNow.name else document.subcjectName
    var level = document.lev.toString
    var department = document.department
    var docContent = document.content

    def save() {
      //println("-------------save------------title-----------------")
      if (isOwner) {
        document.title = title
        document.descript = descript
        if(document.subjectId == 0L) document.subjectId = subjectNow.id
        document.lev = level.toInt
        document.subcjectName = findSubjectName(document.subjectId)
        document.authorId = user.id.is
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
    var departs = if(document.subjectId == 0L) subjectNow.departments.map(d => (d, d))
    else  subjectTeach.find(s => s.id == document.subjectId).getOrElse(subjectNow)
    	.departments.map(d => (d,d))
    "#docID" #> SHtml.text(docID, docID = _) &
      "#docTitle" #> SHtml.text(title, title = _) &
      "#docDescription" #> SHtml.textarea(descript, descript = _) &
      "#subject" #> SHtml.text(subject, x => Unit) &
      "#docLevel" #> SHtml.select(levList, Full(level), level = _) &
      "#department" #> SHtml.select(departs, Full(department), department = _) & 
      "#docContent" #> SHtml.textarea(docContent, d => docContent = d.trim) &
      "#docSave" #> SHtml.button(<span class="glyphicon glyphicon-floppy-save"></span>  
          ++ Text(" Zapisz"), save,"title"->"Zapisz") &
      "#docDelete" #> SHtml.button(<span class="glyphicon glyphicon-trash"></span>  
          ++ Text(" Usuń "),  delete,"title"->"Usuń") 
  }

}
