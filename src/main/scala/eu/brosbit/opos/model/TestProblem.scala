package eu.brosbit.opos.model

import eu.brosbit.opos.lib.ZeroObjectId
import net.liftweb.json.DefaultFormats
import net.liftweb.json.Serialization.write
import net.liftweb.mongodb.{DateSerializer, MongoDocument, MongoDocumentMeta, ObjectIdSerializer}
import org.bson.types.ObjectId

object TestProblem extends MongoDocumentMeta[TestProblem] {
  override def collectionName = "Problems"
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = TestProblem(ObjectId.get, "", "", "", 0L, Nil, Nil)
}

case class TestProblem(var _id: ObjectId, var description: String, var title: String, var info: String, var author: Long,
                       var inputs: List[String], var expectedOutputs: List[String]) extends MongoDocument[TestProblem] {
  def meta = TestProblem
  import net.liftweb.json.DefaultFormats
  import net.liftweb.json.Serialization.write
  implicit val format = DefaultFormats
  def testsToJson = s""" {"input": ${write(inputs)}, "output": ${write(expectedOutputs)}} """
}