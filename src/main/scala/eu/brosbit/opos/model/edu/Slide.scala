
package eu.brosbit.opos.model.edu

import _root_.net.liftweb.mongodb._
import java.util.Date
import org.bson.types.ObjectId

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
  def exportJsonString =
    s""" "_id":"${_id.toString}", "title":"${title}", "descript":"$descript", "subjectName":"$subjectName",
       |"department":"$department", "slides":"${slides.toString}", "lev":"$lev",
       |""".stripMargin
}
