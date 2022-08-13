
package eu.brosbit.opos.model.edu

import _root_.net.liftweb.mongodb._
import eu.brosbit.opos.lib.ZeroObjectId
import org.bson.types.ObjectId

import scala.util.parsing.json.JSONFormat._

object Presentation extends MongoDocumentMeta[Presentation] {
  override def collectionName = "presentation"

  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer

  def create = new Presentation(ObjectId.get, 0L, 0L, "", 0, "", "", "", "")
}

case class Presentation(var _id: ObjectId, var authorId: Long, var subjectId: Long,
                 var subjectName: String, var lev: Int, var department: String,
                 var title: String, var descript: String, var slides: String)
  extends MongoDocument[Presentation] {
  def meta = Presentation
  def strJson =
    s"""{"_id":"${_id.toString}", "title":"${quoteString(title)}", "descript":"${quoteString(descript)}", "subjectName":"${quoteString(subjectName)}",
       |"department":"${quoteString(department)}", "slides":"${slides.toString}", "lev":"$lev"}
       |""".stripMargin
}
