package pl.brosbit.api

import net.liftweb._
import common._
import util._
import http._
import pl.brosbit._
import java.io.{ ByteArrayInputStream, ByteArrayOutputStream, File }
import provider.servlet.HTTPServletContext
import _root_.net.liftweb.mongodb.DefaultMongoIdentifier
import model.{DocTemplateHead, DocTemplateContent}
import scala.xml.Unparsed

object TemplateDocumentCreater {

  def create(id: String): Box[LiftResponse] = {
    var documentHTML =  getAllTemplates(id)   
    val inputStream = new ByteArrayInputStream(documentHTML.getBytes)
    if(inputStream.available() < 10){
      Full(NotFoundResponse("Not found"))
    } 
    else {
      Full(StreamingResponse(inputStream, () => (), inputStream.available().toLong, ("Content-Type", "text/html") :: Nil, Nil, 200))
    }
  }
  
  private def getAllTemplates(id:String) = {
    DocTemplateHead.find(id) match {
      case Some(docHead) => {
        DocTemplateContent.find(docHead.content) match {
          case Some(docContent) => {

              val documents = docContent.content.map(_.template)
            val content:String = if(docHead.tab) docHTMLasTable(documents, docHead.template)
              else docHTMLasDiv(documents)
        	  """<html><head><title> %s </title>
            	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8" /></head><body> 
        			 %s </body></html>""".format( docHead.title, content)
           }
          case _ => "Brak dokumentu"
          }
        }
      case _ => "Brak dokumentu"
      }
    }


  private def docHTMLasDiv(docs:List[String]) = {
    docs.mkString("\n")
  }

  private def docHTMLasTable(docs:List[String], templ:String) = {
    val reg = "<tr>(*)</tr>".r
    val trs = docs.map(d => {
      val res =  reg.findAllIn(d)
      "<tr>" + res.slice(1,res.length).mkString(" ") + "</tr>"
    })
    val ths = reg.findAllIn(templ).slice(0,1).mkString("")
    "<table><thead><tr>%s</tr><thead><tbody>%s</tbody></table>".format(trs.mkString(""), ths)
  }

}