package pl.edu.osp.lib

import pl.edu.osp.model._
import pl.edu.osp.model.edu._
import _root_.net.liftweb._
import mongodb._
import com.mongodb.gridfs._
import com.mongodb.DBCursor
import org.bson.types.ObjectId


class GCFiles {
  var pathImgList: List[String] = Nil


  def calculateNotUsed() {
    val imgResoures = ImagesResource.findAll

  }

  def deleteNotUsed() {

  }

  private def filesNotUsed() {
  }

  private def getFileList = MongoDB.use(DefaultMongoIdentifier) {
    db =>
      val fs = new GridFS(db)
      val currsor = fs.getFileList()
      while (currsor.hasNext) {
        //val dbObject = currsor.next()
        val gfsFile = fs.find(currsor.next()).get(0)
        val mime = gfsFile.getContentType().toLowerCase
        //println("MIME TYPE::::: " + mime)
        //if(mime == ".jpg" || mime == ".png" || mime == ".gif") {
          pathImgList = gfsFile.getId().asInstanceOf[ObjectId].toString()   :: pathImgList
        //}
      }
  }

  private def getImagesAll = MongoDB.use(DefaultMongoIdentifier) {
    db =>
      val fs = new GridFS(db)
      val currsor = fs.getFileList()
      while (currsor.hasNext) {
        //val dbObject = currsor.next()
        val gfsFile = fs.find(currsor.next()).get(0)
        val mime = gfsFile.getContentType().toLowerCase
        println("MIME TYPE::::: " + mime)
        if(mime == "image/jpeg" || mime == "image/png" || mime == "image/gif") {
        pathImgList = gfsFile.getId().asInstanceOf[ObjectId].toString()   :: pathImgList
        }
      }
  }

  private def getAllImagesInSlides(): List[String] = {
    var imgSlides: List[String] = Nil
    val set: Set[String] = Set()
    val slidesStr = SlideContent.findAll.map(_.slides)
    //use regex to find all images /image/\d{12}\.[a-z]{3,4}
    imgSlides
  }

  //for test
  def getAllInGridFS() = {
    getImagesAll
    pathImgList
  }

}