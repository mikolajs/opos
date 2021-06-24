package eu.brosbit.opos.model.edu

import _root_.net.liftweb.mongodb._
import java.util.Date
import org.bson.types.ObjectId
import scala.util.parsing.json.JSONFormat._

//authors - last is older
object SlideContent extends MongoDocumentMeta[SlideContent] {
  override def collectionName = "slidesContent"

  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer

  def create = new SlideContent(ObjectId.get, "")
}

case class SlideContent(var _id: ObjectId, var slides: String) extends MongoDocument[SlideContent] {
  def meta = SlideContent
  def strJson =
    s"""{"_id":"${_id.toString}","slides":"${quoteString(slides.trim)}"}
     |""".stripMargin
}
