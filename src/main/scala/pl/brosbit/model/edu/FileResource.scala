package pl.brosbit.model.edu

import _root_.net.liftweb.mongodb._
import org.bson.types.ObjectId
import java.util.Date


object FileResource extends MongoDocumentMeta[FileResource] {
  override def collectionName = "fileresource"
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create(fileId:ObjectId)  
  		= new FileResource(ObjectId.get,fileId,  0L, "", 0L,"","")
}

case class FileResource(var _id: ObjectId, var fileId:ObjectId, var authorId:Long,  
         var subjectName:String, var subjectId:Long, var title: String, var descript:String
        ) extends  MongoDocument[FileResource] {
  def meta = FileResource
}
