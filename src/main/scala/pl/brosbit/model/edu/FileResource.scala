package pl.brosbit.model.edu

import _root_.net.liftweb.mongodb._
import org.bson.types.ObjectId
import java.util.Date


object FileResource extends MongoDocumentMeta[FileResource] {
  override def collectionName = "fileresource"
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create  
  		= new FileResource(ObjectId.get,new ObjectId("000000000000000000000000"),  0L, 0, "", 0L,"","", "", "")
}

case class FileResource(var _id: ObjectId, var fileId:ObjectId, var authorId:Long, var lev:Int,
         var subjectName:String, var subjectId:Long, var title: String, var descript:String,
       var  mime: String, var department:String) extends  MongoDocument[FileResource] {
  def meta = FileResource
}
