/*
 * Copyright (C) 2011   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register - Wirtualny Dziennik)
 *   LICENCE: GNU AFFERO GENERAL PUBLIC LICENS Version 3 (AGPLv3)
 *   See: <http://www.gnu.org/licenses/>.
 */

package pl.brosbit.model.page

import _root_.net.liftweb.mongodb._
import java.util.Date
import org.bson.types.ObjectId
import _root_.pl.brosbit.model._
import _root_.net.liftweb.json.JsonDSL._
import _root_.net.liftweb.util.Helpers._

case class Slide(src:String, link:String, title:String)
case class NewInForum(link:String, title:String)
case class NewInNews(link:String, title:String)
case class Anounce(title:String, content:String)

case class MainPageNewInfo(val slides:List[Slide], 
							val forum:List[NewInForum], val news: List[NewInNews], 
							 val anounces: List[Anounce])	 

object Keys extends Enumeration {
  val anounce = Value("anounce")
  val news = Value("article")
  val slide = Value("slide")
  val forum = Value("forum")
}

object MainPageData extends MongoDocumentMeta[MainPageData] {
  override def collectionName = "mainpagedata"
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = MainPageData(ObjectId.get, "", 0, "", "", "", "")
  
  def getMainPageNewInfo() = {
    var links:List[Link] = Nil
    var anounces:List[Anounce] = Nil
    var slides:List[Slide] = Nil
    var news:List[NewInNews] = Nil
    var forums:List[NewInForum] = Nil
    
    MainPageData.findAll.foreach(mainPageData => {
      mainPageData.key match {
        case key if(key == Keys.anounce.toString) => anounces = procesAnounce(mainPageData)::anounces
        case key if(key == Keys.news.toString) => news = procesNews(mainPageData)::news
        case key if(key == Keys.slide.toString) => slides = procesSlide(mainPageData)::slides
        case key if(key == Keys.forum.toString) => forums = procesForum(mainPageData)::forums
        case _  => 
      }
      
    })
   MainPageNewInfo(slides, forums, news, anounces)
  }
  
  def procesSlide(mainPageData:MainPageData):Slide = {
    var src = mainPageData.src
    var link = mainPageData.link
    var title = mainPageData.title
    Slide(src, link, title)
  }
  
   def procesAnounce(mainPageData:MainPageData):Anounce = {
    var title = mainPageData.title
    var content = mainPageData.content
    Anounce(title, content)
  }
  
   def procesNews(mainPageData:MainPageData):NewInNews = {
    var link = mainPageData.link
    var title = mainPageData.title
    NewInNews(link, title)
  }
  
   def procesForum(mainPageData:MainPageData):NewInForum = {
    var link = mainPageData.link
    var title = mainPageData.title 
    NewInForum(link, title)
  }
  
}

case class MainPageData(_id: ObjectId, var key : String, var id:Int, 
				    var link:String, var content:String, var title:String, 
				    var src:String) extends MongoDocument[MainPageData] {
  def meta = MainPageData
}


							

