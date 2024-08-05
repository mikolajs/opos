package eu.brosbit.opos.model.page

import net.liftweb.json.Formats
import net.liftweb.mongodb.{DateSerializer, MongoDocument, MongoDocumentMeta, ObjectIdSerializer}
import org.bson.types.ObjectId


object LastImports extends MongoDocumentMeta[LastImports] {
  override def collectionName = "lessonThemes"

  override def formats: Formats = super.formats + new ObjectIdSerializer + new DateSerializer

  def create: LastImports = LastImports(ObjectId.get, 0L, 0L, "", isJson = false, 0, "", Map())
}

case class LastImports(var _id:ObjectId,  var teacherId: Long, var date:Long,  var fileName:String, var isJson: Boolean,
                        var objectNumber: Int, var errors: String, var docJson: Map[String, Int]) extends  MongoDocument[LastImports]{
  def meta: LastImports.type = LastImports
}


