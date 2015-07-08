package pl.brosbit.snippet.admin

import java.util.Date
import scala.xml.{NodeSeq, Text, XML, Unparsed}
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.pl.brosbit.model.page._
import _root_.pl.brosbit.model._
import _root_.net.liftweb.http.{S, SHtml, RequestVar}
import _root_.net.liftweb.mapper.{Ascending, OrderBy, By}
import _root_.net.liftweb.http.js._
import JsCmds._
import JE._
import Helpers._
import org.bson.types.ObjectId
import _root_.net.liftweb.json.JsonDSL._

class AdminAddSlidesSn {

  def addSlide() = {
    var id = ""
    var descript = ""
    var src = ""
    var htmlContent = ""

    def save() {
      val mainPageSlide = MainPageSlide.find(id) match {
        case Some(mainPageSlide) => mainPageSlide
        case _ => MainPageSlide.create
      }
      mainPageSlide.desc = descript
      mainPageSlide.img = src
      mainPageSlide.html = htmlContent
      mainPageSlide.save
    }

    def delete() {
      MainPageData.find(id) match {
        case Some(mainPageData) => mainPageData.delete
        case _ =>
      }
    }

    "#id" #> SHtml.text(id, id = _, "style" -> "display:none;") &
      "#link" #> SHtml.text(src, src = _) &
      "#htmlContent" #> SHtml.textarea(htmlContent, htmlContent = _) &
      "#description" #> SHtml.text(descript, descript = _) &
      "#save" #> SHtml.submit("Zapisz!", save) &
      "#delete" #> SHtml.submit("Usuń!", delete)
  }

  def slideList() = {
    val slides = MainPageSlide.findAll
    "tr" #> slides.map(slide => {
      <tr id={slide._id.toString} ondblclick="setData(this);">
        <td>
          <img src={slide.img} style="width:300px;height:100px;"/>
        </td>
        <td>
          {slide.desc}
        </td> <td>
        {Unparsed(slide.html)}
      </td>
      </tr>
    })
  }


}