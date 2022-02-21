package eu.brosbit.opos.model

import eu.brosbit.opos.lib.ZeroObjectId
import net.liftweb.mongodb.{DateSerializer, MongoDocument, MongoDocumentMeta, ObjectIdSerializer}
import org.bson.types.ObjectId

object TestProblemTry extends MongoDocumentMeta[TestProblemTry] {
  override def collectionName = "TestProblemTry"
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = TestProblemTry(ObjectId.get, ZeroObjectId.get, 0, "", false, "", Nil, 0L, Nil)
}

case class TestProblemTry(var _id: ObjectId, var problem: ObjectId, var author: Long, var code: String,
                          var checked: Boolean, var lang: String, var good: List[Boolean], var aDate: Long,
                          var outputs: List[String]) extends MongoDocument[TestProblemTry] {
  def meta = TestProblemTry
}
