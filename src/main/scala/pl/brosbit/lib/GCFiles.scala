package pl.brosbit.lib

import pl.brosbit.model._
import pl.brosbit.model.edu._
import _root_.net.liftweb._
import mongodb._
import com.mongodb.gridfs._
import com.mongodb.DBCursor
import org.bson.types.ObjectId


class GCFiles {
   var pathImgList:List[String] = Nil
   
    
  
   def calculateNotUsed(){
       val imgResoures = ImagesResource.findAll  
       
   }
   
   def deleteNotUsed(){
       
   }
   
   private def filesNotUsed(){
         }
   
   private def getFileList = MongoDB.use(DefaultMongoIdentifier) { db =>
         val fs = new GridFS(db)
         val currsor = fs.getFileList()
        while(currsor.hasNext){
          //val dbObject = currsor.next()
          val gfsFile = fs.find(currsor.next()).get(0)
          pathImgList = "/image/" + gfsFile.getId().asInstanceOf[ObjectId].toString() + 
          			gfsFile.getContentType() :: pathImgList
       }
   }
   
   private def getAllImagesInSlides():List[String] = {
       var imgSlides:List[String] = Nil
       val set:Set[String] = Set()
       val slidesStr = SlideContent.findAll.map(_.slides)
       //use regex to find all images /image/\d{12}\.[a-z]{3,4}
       imgSlides
   }
   //for test
   def getAllInGridFS() = {
       filesNotUsed
       pathImgList
   }
  
}