package pl.brosbit.snippet.admin

import java.util.Date
import scala.xml.{ NodeSeq, Text, XML, Unparsed }
import _root_.net.liftweb.util._
import _root_.pl.brosbit.lib.MailConfig
import _root_.net.liftweb.common._
import _root_.pl.brosbit.model.page._
import _root_.pl.brosbit.model._
import _root_.net.liftweb.http.{ S, SHtml, RequestVar }
import _root_.net.liftweb.mapper.{ Ascending, OrderBy, By }
import _root_.net.liftweb.http.js._
import JsCmds._
import JE._
import Helpers._
import org.bson.types.ObjectId
import _root_.net.liftweb.json.JsonDSL._

class AdminEditLinksSn {
  
	 def addLinks() = {
    var xmlDataStr = ""
    def add() {
      val mainPageLinks = getMainPageLinks
      var linkDepartments:List[LinkDepartment] = Nil
      var xmlData = XML.loadString(xmlDataStr)
      (xmlData \ "links").foreach(links => {
        val nameDep = (links \ "@name").toString
        var linksList:List[Link] = Nil 
        (links \ "link").foreach(link => {
          val title = (link \ "@title").toString
          val url = (link \ "@src").toString
          linksList = Link(url, title) :: linksList 
        })
        linkDepartments = LinkDepartment(nameDep, linksList) :: linkDepartments
      })
      mainPageLinks.links = linkDepartments
      mainPageLinks.save
      S.redirectTo("/admin/links")
    }
    "#xmlData" #> SHtml.text(xmlDataStr, xmlDataStr = _, "style" -> "display:none;") &
      "#submit" #> SHtml.submit("ZAPISZ", add, "onclick" -> "return saveAll()")
  }

  def showLinks() = {
    val mainPageLinks = getMainPageLinks
   
        ".divDepartment" #> mainPageLinks.links.map(linkGroup => {
      ".nameDep" #> <input value={linkGroup.name} type="text"/> &
      ".linkTr" #> linkGroup.links.map(link => {
        	".nameLink" #> <td>{ link.title}</td> &
            ".urlLink" #> <td>{ link.url}</td>
        	})
        })
    }
  
  private def getMainPageLinks = MainPageLinks.findAll match {
    case head::list => head
    case _ => MainPageLinks.create
  }
   
}
