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

class AdminDepartmentsSn {

   def addDepartment() = {
    var id = ""
    var nrStr = ""
    var name = ""

    def addDepartment() {
       PageDepartment.find(id.trim) match {
        case Some(pageDepartment) => {
          pageDepartment.name = name
          pageDepartment.nr = tryo(nrStr.toInt).openOr(99)
          pageDepartment.save
        }
        case _ => {
          val pageDepartment = PageDepartment.create
          pageDepartment.name = name
          pageDepartment.nr = tryo(nrStr.toInt).openOr(99)
          pageDepartment.save
        }
      }
       S.redirectTo("/admin/pages")
    }
    
    
    def delDepartment(): Unit = {
        PageDepartment.find(id.trim) match {
          case Some(pageDepartment) => pageDepartment.delete
          case _ => 
        }
        S.redirectTo("/admin/pages")
    }

    "#id" #> SHtml.text(id, x => id = x, "style" -> "display:none;", "id" -> "id") &
    "#order" #> SHtml.text(nrStr, x => nrStr= x.trim, "maxlength" -> "2") &
      "#name" #> SHtml.text(name, x => name = x.trim, "maxlength" -> "30", "id" -> "name") &
      "#save" #> SHtml.submit("Zapisz!", addDepartment, "onclick" -> "return validateForm()") &
      "#delete" #> SHtml.submit("Usuń!", delDepartment, 
          "onclick" -> "return confirm('Na pewno chcesz usunąć dział i wszystkie strony działu?');")
  }

  def departments(n: NodeSeq): NodeSeq = {
    val departments = PageDepartment.findAll
    var node: NodeSeq = <tbody>{
      for (department <- departments) yield {
        <tr ondblclick={ "setData(this)" } id={department._id.toString()} >
          <td>{department.nr.toString}</td><td>{department.name}</td>
        </tr>
      }
    }</tbody>
    node
  }

}