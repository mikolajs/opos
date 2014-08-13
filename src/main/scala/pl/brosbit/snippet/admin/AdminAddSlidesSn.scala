package pl.brosbit.snippet.admin

import java.util.Date
import scala.xml.{ NodeSeq, Text, XML, Unparsed }
import _root_.net.liftweb.util._
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

class AdminAddSlidesSn {
	
  def addSlide() = {
    var id = ""
    var descript = ""
    var src = ""
    var link = ""
      
    def save(){
      val mainPageData = MainPageData.find(id) match {
        case Some(mainPageData) => mainPageData
        case _ => {
          val tmpMainPageData = MainPageData.create
          tmpMainPageData.key = Keys.slide.toString
          tmpMainPageData
        }
      }
      mainPageData.title = descript
      mainPageData.src = src
      mainPageData.link = link
      mainPageData.save
    }
    
    def delete() {
      MainPageData.find(id) match {
        case Some(mainPageData) => mainPageData.delete
        case _ =>
      }
    }
    
    "#id" #> SHtml.text(id, id = _, "style"->"display:none;") &
    "#source" #> SHtml.text(src, src = _) &
    "#link" #> SHtml.text(link, link = _) &
    "#description" #> SHtml.text(descript, descript = _) &
    "#save" #> SHtml.submit("Zapisz!", save) &
    "#delete" #> SHtml.submit("Usuń!", delete) 
  }
  
  def slideList() = {
    val slides = getMainPageDataWithSlides
    "tr" #> slides.map(slide => {
    		<tr id={slide._id.toString} ondblclick="setData(this);" >
    		<td><img src={slide.src} style="width:300px;height:100px;" /></td>
    		<td>{slide.link}</td><td>{slide.title}</td></tr>
    	})
  }
  
  
  
  
  private def getMainPageDataWithSlides = MainPageData.findAll(("key"->Keys.slide.toString))
  
}