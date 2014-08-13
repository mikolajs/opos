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


class CreateTemplateSn extends BaseTeacher {
  
  var id = S.param("id").getOrElse("0")
  var title = ""
  var comment = ""
  var table = false
  var template = ""
  DocTemplateHead.find(id) match {
    case Some(templateHead) => {
      title = templateHead.title
      comment = templateHead.comment
      template = templateHead.template
    }
    case _ => {
    }
  }
  
  
  def edit() = {
    
    def save(){
        var docHead = DocTemplateHead.find(id) match {
        	case Some(templateHead) => templateHead
        	case _ => DocTemplateHead.create
        }
    	var docContent = DocTemplateContent.find(docHead.content) match {
    		case Some(templateContent) => templateContent
    		case _ => DocTemplateContent.create
    	}
      docHead.title = title.trim
      docHead.comment = comment.trim
      docHead.content = docContent._id
      docHead.template = template.trim
      docHead.save
      docContent.save
      S.redirectTo("/teacher/doctemplate/" + id)
    }
    
    def delete(){
      if(id.length > 20){
        DocTemplateHead.find(id) match {
          case Some(templateHead) => {
            DocTemplateContent.find(templateHead.content) match {
              case Some(templateContent) => templateContent.delete
              case _ =>
            }
            templateHead.delete
          }
          case _ =>
        }
      }
      S.redirectTo("/teacher/doctemplate")
    }
    
    def discard(){
      S.redirectTo("/teacher/doctemplate")
    }
    
    "#id" #> SHtml.text(id, id = _) &
    "#title" #> SHtml.text(title, title = _) &
    "#comment" #> SHtml.textarea(comment, comment = _) &
    "#template" #> SHtml.textarea(template, template = _) &
    "#save" #> SHtml.submit("Zapisz", save) &
    "#delete" #> SHtml.submit("Usuń", delete) &
    "#discard" #> SHtml.submit("Porzuć", discard)
  }
  
  
}
