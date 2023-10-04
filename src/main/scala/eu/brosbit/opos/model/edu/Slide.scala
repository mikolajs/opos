
package eu.brosbit.opos.model.edu

import _root_.net.liftweb.mongodb._
import org.bson.types.ObjectId
import scala.util.parsing.json.JSONFormat._

///TODO: DELETE??
object Slide extends MongoDocumentMeta[Slide] {
  override def collectionName = "slides"

  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer

  def create = new Slide(ObjectId.get, 0L, 0L, "", 0, "", "", "", new ObjectId("000000000000000000000000"))
}

case class Slide(var _id: ObjectId, var authorId: Long, var subjectId: Long,
                 var subjectName: String, var lev: Int, var department: String,
                 var title: String, var descript: String, var slides: ObjectId)
  extends MongoDocument[Slide] {
  def meta = Slide
  def strJson =
    s"""{"_id":"${_id.toString}", "title":"${quoteString(title)}", "descript":"${quoteString(descript)}", "subjectName":"${quoteString(subjectName)}",
       |"department":"${quoteString(department)}", "slides":"${slides.toString}", "lev":"$lev"}
       |""".stripMargin
}
