package pl.brosbit.snippet.page

import _root_.scala.xml.{ NodeSeq, Unparsed}
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.pl.brosbit.model.page._
import _root_.pl.brosbit.model._
import _root_.net.liftweb.mapper.{ Descending, OrderBy, By }
import _root_.net.liftweb.http.{ S, SHtml}
import Helpers._
import _root_.pl.brosbit.lib.{ Formater }
import java.util.Date
import _root_.net.liftweb.json.JsonDSL._
import _root_.net.liftweb.mongodb._
import org.bson.types.ObjectId
import com.mongodb.BasicDBObject
import pl.brosbit.model.page._

class Test {
	
  def test1() = {
    val fth = ForumThreadHead.findAll.head
    val format = """ id: %s, count: %d """
    val text1 = format.format(fth._id.toString, fth.count)
    val idObjStr = "ObjectId(\"" + fth._id.toString + "\")"
    val o3 = (("$inc" -> ("count" -> 1)) )
    ForumThreadHead.update(("_id"->fth._id.toString), o3)
    val fth2 = ForumThreadHead.find(fth._id).get
     val text2 = format.format(fth2._id.toString, fth2.count)
     "#info" #> <p>Object: {idObjStr} <br/>{text1}<br/>{text2}</p>
  }
  
  def test2() = {
      (new pl.brosbit.lib.TestData).createData
       "#info" #> <p>Data Created!</p>
  }
  

}