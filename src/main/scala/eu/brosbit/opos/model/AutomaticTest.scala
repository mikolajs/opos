package eu.brosbit.opos.model
import _root_.net.liftweb.mongodb._
import eu.brosbit.opos.lib.ZeroObjectId
import org.bson.types.ObjectId

object AutomaticTest extends MongoDocumentMeta[AutomaticTest] {
  override def collectionName = "AutomaticTest"
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = AutomaticTest(ObjectId.get, ZeroObjectId.get, 0L, "", 0L, Nil)
}

case class AutomaticTest(var _id: ObjectId, var problemId: ObjectId, var pupilId: Long, var pupilName: String,
                    var date: Long, var outputs:List[String]) extends MongoDocument[AutomaticTest] {
  def meta = AutomaticTest
}
