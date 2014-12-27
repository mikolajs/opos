package pl.brosbit.model


import _root_.net.liftweb.mongodb._
import org.bson.types.ObjectId

//dest: t - for teachers announce, p - for pupils announce, i - individual message
object UserMessages extends MongoDocumentMeta[UserMessages] {
  override def collectionName = "userMessages"
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = UserMessages(ObjectId.get, 0L, "", Nil, Nil)
}

case class UserMessages(var _id: ObjectId, var userId:Long,  var userName: String,
                   var messLatest: List[String], var messOld: List[String]
                    ) extends MongoDocument[UserMessages] {
  def meta = UserMessages

}