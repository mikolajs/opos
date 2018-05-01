package pl.edu.osp.lib

import pl.edu.osp.model._
import pl.edu.osp.model.edu._
import _root_.net.liftweb._
import mongodb._
import com.mongodb.gridfs._
import com.mongodb.DBCursor
import org.bson.types.ObjectId
import pl.edu.osp.model.page.ArticleContent

/* to CLear files not used:
 *  look all: lessons notes, articles, slides and exercises, exams answers with paths of anchor for /img or /file
 *  ?? forum, massages
 */

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
        //println("MIME TYPE::::: " + mime)
        if(mime == "image/jpeg" || mime == "image/png" || mime == "image/gif") {
        pathImgList = gfsFile.getId().asInstanceOf[ObjectId].toString()   :: pathImgList
        }
      }
  }

  private def getAllFilesInSlides(): List[String] = {
    var imgSlides: List[String] = Nil
    val set: Set[String] = Set()
    val slidesStr = SlideContent.findAll.map(_.slides)
    //use regex to find all images /img/\d{12}\.[a-z]{3,4}
    slidesStr.foreach(str => {
      imgSlides = imgSlides ++ extractIDFilse(str)
    })
    println("SLIDES FILE ID: " + imgSlides.mkString("\n"))
    imgSlides
  }

  private def getAllFilesInArticles() = {
    var dataStr: List[String] = Nil
    val slidesStr = ArticleContent.findAll.map(_.content)
    //use regex to find all images /img/\d{12}\.[a-z]{3,4}
    slidesStr.foreach(str => {
      dataStr = dataStr ++ extractIDFilse(str)
    })
    println("SLIDES FILE ID: " + dataStr.mkString("\n"))
    dataStr
  }

  private def extractIDFilse(dataStr:String): List[String] ={
    val reg = "/img/[a-z,0-9]{24}".r
    reg.findAllIn(dataStr).toList.map(s => s.split('/')(2))

  }

  //for test
  def getAllInGridFS() = {
   // getAllFilesInSlides()
    //getAllFilesInArticles()
    //notes
    //files in answer students
    //wi
    getImagesAll
    pathImgList
  }

}