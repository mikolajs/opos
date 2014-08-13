package pl.brosbit.snippet.teacher

import java.util.Date
import scala.xml.Unparsed
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.pl.brosbit.model.page._
import _root_.pl.brosbit.model._
import _root_.pl.brosbit.lib.Formater
import _root_.net.liftweb.http.{ S, SHtml }
import Helpers._
import org.bson.types.ObjectId
import _root_.net.liftweb.json.JsonDSL._
import  _root_.net.liftweb.http.js.JsCmds._
 import  _root_.net.liftweb.http.js.JsCmd
 import  _root_.net.liftweb.http.js.JE._


class DocTemplateSn extends BaseTeacher {
	
  var id = S.param("id").getOrElse("0")
  var docHead = DocTemplateHead.find(id) match {
    case Some(templateHead) => templateHead
    case _ => {
      val docList = DocTemplateHead.findAll
      if(!docList.isEmpty) docList.head
      else DocTemplateHead.create
    }
  }
  var docContent = DocTemplateContent.find(docHead.content) match {
    case Some(templateContent) => templateContent
    case _ => DocTemplateContent.create
  }
  
  def showHead() = {
    "span *" #> docHead.title &
    "#comment *" #> Unparsed(docHead.comment)
  }
  
  def addEntry() = {
    var userTemplate = findCurrentUserTemplate
    def save(){
      val newTemplatePiece = TemplatePiece(user.id.toString, user.getFullName, userTemplate)
      DocTemplateContent.update(("_id"->docContent._id.toString),
          ("$pull"->("content" -> ("userId"->user.id.toString))))
      DocTemplateContent.update(("_id"->docContent._id.toString),
          ("$addToSet"->("content"->newTemplatePiece.mapString)))
    }
    
    def delete(){
      DocTemplateContent.update(("_id"->docContent._id.toString),
          ("$pull"->("content" -> ("userId"->user.id.toString))))
    }
    
    "#addentry" #> SHtml.textarea(userTemplate, userTemplate = _) &
    "#save" #> SHtml.submit("Zapisz", save) &
    "#delete" #> SHtml.submit("Usuń", delete)
    
  }
  
  def fullDocument() = {
    ".fulldocument *" #>  docContent.content.map(template => {
      <div><h2> Dodane przez: {template.userName} </h2><hr/>{Unparsed(template.template)}</div>
    } )  
  }
  
  def docTemplates() = {
    "li" #> DocTemplateHead.findAll.map(templateHead => {
      <li><a href={"/teacher/doctemplate/" + templateHead._id.toString}>{templateHead.title}</a></li>
    })
  }
  
  def adminMenu() = {
    "ul" #> {if(isAdmin) {<ul>
    		<li id="createTemplate"><a href="/teacher/createtemplate/0">Utwórz nowy</a></li>
       		<li id="editTemplate"><a href={"/teacher/createtemplate/" + docHead._id.toString}>Edytuj aktualny</a></li>
       	    <li id="importTemplate"><a href={"/getdocument/" +  docHead._id.toString}>Pobierz</a></li>
    		</ul> }
            else {<ul></ul>}}
  }
  
  private def findCurrentUserTemplate:String = {
    val userTemplateList = docContent.content.filter(_.userId == user.id.toString)
    if(userTemplateList.isEmpty){
      docHead.template
    }
    else userTemplateList.head.template  
  } 
  
}
