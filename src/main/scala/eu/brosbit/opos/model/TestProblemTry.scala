package eu.brosbit.opos.model

import eu.brosbit.opos.lib.ZeroObjectId
import net.liftweb.json.DefaultFormats
import net.liftweb.mongodb.{DateSerializer, MongoDocument, MongoDocumentMeta, ObjectIdSerializer}
import org.bson.types.ObjectId

object TestProblemTry extends MongoDocumentMeta[TestProblemTry] {
  override def collectionName = "TestProblemTry"
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = TestProblemTry(ObjectId.get, ZeroObjectId.get, 0, "", false, "", false, 0L, false, 0L, "",  "")
}

case class TestProblemTry(var _id: ObjectId, var problem: ObjectId, var author: Long, var code: String,
                          var checked: Boolean, var lang: String, var good: Boolean, var aDate: Long,
                          var running: Boolean, var timeRun: Long, var authorInfo: String,
                          var outputs: String) extends MongoDocument[TestProblemTry] {
  def meta = TestProblemTry
  def jsonStr =
    s"""{"id":"${_id.toString}", "auth":"$author", "authInfo":"$authorInfo", "code":"${code.replace("\"", "\\\"").replace("\n", "\\n").replace("\r","\\r")}" }"""}
