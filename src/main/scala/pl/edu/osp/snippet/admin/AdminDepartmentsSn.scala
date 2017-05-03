package pl.edu.osp.snippet.admin

import scala.xml.{NodeSeq, Unparsed}
import _root_.net.liftweb.util._

import _root_.pl.edu.osp.model.page._
import _root_.net.liftweb.http.{S, SHtml}
import _root_.net.liftweb.json.JsonDSL._
import Helpers._


class AdminDepartmentsSn {

  def addDepartment() = {
    var id = ""
    var nrStr = ""
    var name = ""
    var img = ""
    var info =""
    var fimg = ""
    var finfo = ""

    def addDepartment() {
      val pageDepartment = PageDepartment.find(id.trim) match {
        case Some(pageDepartment) => pageDepartment
        case _ => PageDepartment.create
      }
      pageDepartment.name = name.trim
      pageDepartment.nr = tryo(nrStr.trim.toInt).openOr(99)
      pageDepartment.img = img.trim
      pageDepartment.info = info.trim
      pageDepartment.save
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
      "#order" #> SHtml.text(nrStr, nrStr = _, "maxlength" -> "2") &
      "#name" #> SHtml.text(name, name = _, "maxlength" -> "40", "id" -> "name") &
      "#info" #> SHtml.textarea(info, info = _,  "id" -> "info") &
      "#link" #> SHtml.text(img, img = _ ) &
      "#save" #> SHtml.submit("Zapisz!", addDepartment, "onclick" -> "return validateForm()") &
      "#delete" #> SHtml.submit("Usuń!", delDepartment,
        "onclick" -> "return confirm('Na pewno chcesz usunąć dział i wszystkie strony działu?');")
  }

  def departments(n: NodeSeq): NodeSeq = {
    val departments = PageDepartment.findAll(Nil, "nr"->1)
    val node: NodeSeq = <tbody>
      {for (department <- departments) yield {
        <tr ondblclick={"setData(this)"} title={"ID: " + department._id.toString}
            id={department._id.toString()}>
          <td> {department.nr.toString}</td>
          <td>{department.name}</td>
          <td><img src={department.img} style="width:200px;" /></td>
          <td>{Unparsed(department.info)}</td>
        </tr>
      }}
    </tbody>
    node
  }

}