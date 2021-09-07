package eu.brosbit.opos.api

import net.liftweb._
import common._
import http._
import eu.brosbit.opos._
import java.io.ByteArrayInputStream
import model.{DocTemplate, DocContent}
import _root_.net.liftweb.json.JsonDSL._

object TemplateDocumentCreater {

  def create(id: String): Box[LiftResponse] = {
    val documentHTML = getAllTemplates(id)
    val inputStream = new ByteArrayInputStream(documentHTML.getBytes)
    if (inputStream.available() < 10) {
      Full(NotFoundResponse("Not found"))
    }
    else {
      Full(StreamingResponse(inputStream, () =>
        (), inputStream.available().toLong, ("Content-Type", "text/html") :: Nil, Nil, 200))
    }
  }

  //create html from one document
  private def getAllTemplates(id: String) = {
    DocTemplate.find(id) match {
      case Some(docHead) => {
        val documents = DocContent.findAll(("template" -> docHead._id.toString), ("nr" -> 1)).map(_.content)
        val content: String = if (docHead.tab) docHTMLasTable(documents, docHead.template)
        else docHTMLasDiv(documents)
        """<html><head><title> %s </title>
            	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8" /></head><body> 
        			 %s </body></html>""".format(docHead.title, content)
      }
      case _ => "Brak dokumentu"
    }
  }


  private def docHTMLasDiv(docs: List[String]) = {
    docs.mkString("\n")
  }

  private def docHTMLasTable(docs: List[String], templ: String) = {
    val reg = "<tr>(.+?)</tr>".r

    val trs = docs.map(d => {
      val doc = d.replace("\n", " ").replace("\r", "").replace("\t", "")
      println("DOC::: \n" + doc)
      val res = reg.findAllIn(doc).toList
      res.drop(1).mkString(" ")
    })
    val ths = reg.findAllIn(templ.replace("\n", " ").replace("\r", "").replace("\t", ""))
      .toList.take(1).mkString("")
    "<table border=\"1\"><thead><tr>%s</tr><thead><tbody>%s</tbody></table>".format(ths, trs.mkString(""))
  }

}