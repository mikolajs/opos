package eu.brosbit.opos.lib

import eu.brosbit.opos.model._
import eu.brosbit.opos.model.edu._
import _root_.net.liftweb._
import mongodb._
import com.mongodb.gridfs._
import json.JsonDSL._
import org.bson.types.ObjectId
import eu.brosbit.opos.model.page.{ArticleContent, FlashTile, ImageSlides, LinkTilesMainPage, PageDepartment}

import scala.collection.mutable.ArrayBuffer

/* to CLear files not used:
 *  look all: lessons notes, articles, slides and exercises, exams answers with paths of anchor for /img or /file
 *  ?? forum, massages
 */

case class FileToDelInfo(fileType:String, id:String)

class GCFiles {

  private def getOnlyToDeleteFiles(allInDB:List[FileToDelInfo], toStayId:List[String]) = {
    ///TODO: filter to stay
    //toStayId.foreach(println)
    allInDB.filterNot(fi => toStayId.contains(fi.id))
  }

  def getForExportTeacherFiles(userId:Long) = {
    val sb = new ArrayBuffer[String]()
    sb ++= findAllExists(Presentation.findAll("authorId"->userId).map(_.slides))
    sb ++= findAllExists(Document.findAll("authorId"->userId).map(_.content))
    sb ++= findAllExists(QuizQuestion.findAll("authorId"->userId).map(_.question))
    sb ++= findAllExists(LessonCourse.findAll("author"->userId)
      .map(_.contents.filter(_.what == "n").map(_.descript)).flatten)
    //val teacherExams = Exam.findAll("authorId"->userId).map(_._id.toString)
    //ExamAnswer.findAll("exam"->("$in"->teacherExams)).map(_.attach).map(_.split('/').last.split('.').head)

    sb.toList
  }

  private def getFileList = {
    var pathList:List[FileToDelInfo] = Nil
    MongoDB.use(DefaultMongoIdentifier) {
    db =>
      val fs = new GridFS(db)
      val cursor = fs.getFileList()
      while (cursor.hasNext) {
        //val dbObject = cursor.next()
        val gfsFile = fs.find(cursor.next()).get(0)
        val ft = gfsFile.getFilename.split('.').last
        pathList = FileToDelInfo(ft, gfsFile.getId().asInstanceOf[ObjectId].toString())   :: pathList
      }
    }
    pathList
  }

  private def getAllFilesInPresentations(): List[String] = {
    val slidesStr = Presentation.findAll.map(_.slides)
    findAllExists(slidesStr, "Presentations")
  }

  private def getAllFilesInDocuments() = {
    val docStr = Document.findAll.map(_.content)
    findAllExists(docStr, "Documents")
  }

  private def getAllFilesInQuestions() = {
    val docStr = QuizQuestion.findAll.map(_.question)
    findAllExists(docStr, "Questions")
  }

  private def getAllFilesInSlidesImg() = {
    val docStr = ImageSlides.findAll.map(_.src)
    findAllExists(docStr, "Slides images")
  }

  private def getAllFilesInPSO() = {
    val docStr = PSO.findAll.map(_.urlLink.trim)
    findAllExists(docStr, "PSO")
  }

  private def getAllFilesInThemesPlan() = {
    val docStr = ThemesPlan.findAll.map(_.urlLink.trim)
    findAllExists(docStr, "Themes Plan")
  }

  private def getAllFilesInPages() = {
    val docStr = ArticleContent.findAll.map(_.content)
    findAllExists(docStr, "Main pages")
  }

  private def getAllFilesInLessons() = {
    val docStr = LessonCourse.findAll.map(_.contents.filter(_.what == "n").map(_.descript)).flatten
    findAllExists(docStr, "Notes in Lessons")
  }

  private def getAllFilesInAdminDep() = {
    val docStr = PageDepartment.findAll.map(_.img)
    findAllExists(docStr, "Page departments")
  }

  private def getAllFilesInAdminFlash() = {
    val docStr = FlashTile.findAll.map(_.img)
    findAllExists(docStr, "Flash on pages")
  }

  private def getAllFilesInAdminTiles() = {
    val docStr = LinkTilesMainPage.findAll.map(_.img)
    findAllExists(docStr, "Tiles on pages")
  }

  private def getAllFilesInTestProblems():List[String] = {
    val docStr = TestProblem.findAll.map(_.description)
    findAllExists(docStr, "Problems")
  }

  private def getAllFilesInAnswerExams():List[String] = {
    ExamAnswer.findAll.map(_.attach).map(_.split('/').last.split('.').head)
  }

  private def findAllExists(docStr:List[String], info:String = "") = {
    var dataStr: List[String] = Nil
    docStr.foreach(str => {
      dataStr = dataStr ++ extractIDFiles(str)
    })
    println(s"INFO IN GCFiles: Found in $info  ${dataStr.size} files")
    dataStr
  }
  private def extractIDFiles(dataStr:String): List[String] ={
    val reg = "/(img|file)/[a-z,0-9]{24}".r
    reg.findAllIn(dataStr).toList.map(s => s.split('/')(2))
  }

  private def getAllFilesExists() = {
    getAllFilesInPresentations() ++
    getAllFilesInDocuments() ++
    getAllFilesInSlidesImg() ++
      getAllFilesInPSO() ++
      getAllFilesInThemesPlan() ++
      getAllFilesInAdminTiles() ++
      getAllFilesInAdminFlash() ++
      getAllFilesInAdminDep() ++
      getAllFilesInPages() ++
      getAllFilesInTestProblems() ++
      getAllFilesInAnswerExams() ++
      getAllFilesInLessons() ++
      getAllFilesInQuestions()
  }
  //for test
  def getAllToDelete():List[FileToDelInfo] = {
    //notes
    //files in answer students
    val mustStay = getAllFilesExists()
    val all = getFileList
    println("getAllFiles in database size: " + all.size)
    getOnlyToDeleteFiles(all, mustStay)
  }

}