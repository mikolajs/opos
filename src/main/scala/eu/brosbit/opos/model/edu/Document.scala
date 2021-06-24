package eu.brosbit.opos.model.edu

import _root_.net.liftweb.mongodb._
import org.bson.types.ObjectId
import scala.util.parsing.json.JSONFormat._


object Document extends MongoDocumentMeta[Document] {
  override def collectionName = "documents"

  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer

  def create = new Document(ObjectId.get, "", "", "", "", 0L, "", 0L, "", 0)
}

case class Document(var _id: ObjectId, var title: String, var descript: String, var department: String,
                    var content: String, var authorId: Long, var authorName: String, var subjectId: Long,
                    var subjectName: String, var lev: Int) extends MongoDocument[Document] {
  def meta = Document
  def strJson =
    s""" {"_id":"${_id.toString}", "title":"${quoteString(title)}", "descript":"${quoteString(descript)}", "subjectName":"${quoteString(subjectName)}",
       |"department":"${quoteString(department)}", "content":"${quoteString(content.trim)}", "lev":"$lev"}
       |""".stripMargin
  def json = {
         import net.liftweb.json.Serialization.write
         import net.liftweb.json.DefaultFormats
         implicit val formats = DefaultFormats
         write(this)
       }
}
