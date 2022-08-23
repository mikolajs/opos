package eu.brosbit.opos.snippet.edu

import com.mongodb.BasicDBObject
import com.mongodb.gridfs.GridFS
import eu.brosbit.opos.api.{Exports, ImgFilesSearch, ImportJsonList, JsonExportImport}
import eu.brosbit.opos.lib.Zipper
import eu.brosbit.opos.model.{SubjectName, User}
import eu.brosbit.opos.model.edu.{Document, SubjectTeach}
import net.liftweb.common.{Box, Empty, Full}
import net.liftweb.http.{FileParamHolder, S, SHtml}
import net.liftweb.json.{DefaultFormats, parse}
import net.liftweb.json.Serialization.read
import net.liftweb.mongodb.{DefaultMongoIdentifier, MongoDB}
import net.liftweb.util.CssSel
import net.liftweb.util.Helpers._
import org.bson.types.ObjectId

import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}

case class ElementsInZip(var docs:Int = 0, var presentations: Int = 0, var videos: Int = 0, var questions: Int = 0, var lessons: Int = 0,
                         var files: Int = 0, var problems: Int = 0)
case class ElementsJson(var docs:String = "", var presentations: String = "", var videos: String = "", var questions: String = "",
                        var lessons: String = "", var files: String = "", var problems: String = "")



class ImportSn {
  val user = User.currentUser.getOrElse(S.redirectTo("/"))
  val pathName = "/home/ms/export_user_" + user.id.get +".zip"
  val elementsInZip = ElementsInZip()

  def importingSaved(): CssSel = {
    var subjectNamesActual = SubjectName.findAll().map(_.name.get).mkString(",")
    val data = readFileFromDisk()
    val zipper = new Zipper()
    val mapFiles = zipper.fromZipJsonAndBinary(data)
    var subjectNamesImported = ImportJsonList.getSubjects(mapFiles, elementsInZip).mkString(",")
    var subjectNamesToSave = ""
    def mkImport(): Unit = {
      val (jsonFiles, otherFiles) = mapFiles.partition(e => e.key.split('/').length == 2)
      val inserted = jsonFiles.map(jsonFile => {
        val pathAr = jsonFile.key.split('/')
        if (pathAr.length == 2) {
          pathAr.last.split('.').head match {
            case k if k == Exports.JsonFileNames.Documents.toString => insertDocuments(mapFiles(k))
            case k if k == Exports.JsonFileNames.Presentations.toString => insertPresentation(mapFiles(k))
            case k if k == Exports.JsonFileNames.Videos.toString => insertVideos(mapFiles(k))
            case k if k == Exports.JsonFileNames.Questions.toString => insertQuestions(mapFiles(k))
            case k if k == Exports.JsonFileNames.Lessons.toString => insertLessons(mapFiles(k))
            case k if k == Exports.JsonFileNames.Problems.toString => insertProblems(mapFiles(k))
            case k if k == Exports.JsonFileNames.Courses.toString => insertCourses(mapFiles(k))
            case k => 0
          }
        } else 0
      }).sum
      otherFiles.foreach(otherFile => {
        val pathAr = otherFile.key.split('/')
        if (pathAr.length == 3) {
          saveFileToDB(pathAr.last, otherFile._2)
        }
      })
      S.notice("importData", "Zapisano " + inserted + " dokumentów")
    }
    "#docs *" #> elementsInZip.docs.toString &
      "#presentations *" #> elementsInZip.presentations.toString &
      "#videos *" #> elementsInZip.videos.toString &
      "#questions *" #> elementsInZip.questions.toString &
      "#lessons *" #> elementsInZip.lessons.toString &
      "#problems *" #> elementsInZip.problems.toString &
      "#files *" #> elementsInZip.files.toString &
      "#subjectNamesImported" #> SHtml.text(subjectNamesImported, subjectNamesImported = _) &
      "#subjectNamesActual" #> SHtml.text(subjectNamesActual, subjectNamesActual = _) &
      "#subjectNamesToSave" #> SHtml.text(subjectNamesToSave, subjectNamesToSave = _) &
      "#saveLoadedHidden" #> SHtml.submit("Importuj", mkImport)

  }
  private def mkImportOld(eiz:ElementsInZip) = {
    val data = readFileFromDisk()
    val zipper = new Zipper()
    val mapFiles = zipper.fromZipJsonAndBinary(data)
    val mapKeys = mapFiles.keys
    println("mkIMPORT !!!!!!!!!!!!")
    val (jsonFiles, otherFiles) = mapFiles.partition(e => e.key.split('/').length == 2)
    val inserted = jsonFiles.map( jsonFile => {
      val pathAr = jsonFile.key.split('/')
      if(pathAr.length == 2){
        pathAr.last match {
          case k if k == Exports.JsonFileNames.Documents.toString => insertDocuments(mapFiles(k))
          case k if k == Exports.JsonFileNames.Presentations.toString => insertPresentation(mapFiles(k))
          case k if k == Exports.JsonFileNames.Videos.toString => insertVideos(mapFiles(k))
          case k if k == Exports.JsonFileNames.Questions.toString => insertQuestions(mapFiles(k))
          case k if k == Exports.JsonFileNames.Lessons.toString => insertLessons(mapFiles(k))
          case k if k == Exports.JsonFileNames.Problems.toString => insertProblems(mapFiles(k))
          case k if k == Exports.JsonFileNames.Courses.toString => insertCourses(mapFiles(k))
          case k => 0
        }
      } else 0
    }).sum
    otherFiles.foreach(otherFile => {
      val pathAr = otherFile.key.split('/')
      if(pathAr.length == 3){
        saveFileToDB(pathAr.last, otherFile._2)
      }
    })
  }

  def importingFile: CssSel = {
    var error = ""
    var subjectNames = ""
    def mkUpload(zipFile:FileParamHolder): Unit = {
      if(zipFile.fileName != "export.zip") error = "Błędna nazwa pliku"
      else {
        saveFileToDisk(zipFile.file)
      }
    }
    def emptyFun(): Unit ={
      println("EMPTY FUNCTION!!!")
    }
    "#filezip" #> SHtml.fileUpload( zipFile => mkUpload(zipFile)) &
    "#save"#> SHtml.submit("Wczytaj", () => emptyFun())
  }

  ///change Path to /home/opos/...
  def saveFileToDisk(bytes: Array[Byte]) = {
    val path = Paths.get(pathName)
    Files.write(path, bytes)
  }

  def readFileFromDisk() = {
    val path = Paths.get(pathName)
    Files.readAllBytes(path)
  }

  private def setInfo(json: ElementsJson): String = {
    s""" {${json.docs}, ${json.presentations}, ${json.videos},  ${json.questions}, ${json.lessons}, ${json.files}"""
  }
//TODO: Implement!
  private def insertDocuments(bytes: Array[Byte]) = {
    println(bytes)
    val arr = ImportJsonList.documentList(bytes)
    arr.foreach(docImp => {
      val doc = Document.create
      doc._id = new ObjectId(docImp._id)
      doc.lev = docImp.lev
      doc.title = docImp.title
      doc.content = docImp.content
      doc.descript = docImp.descript
      doc.department = docImp.department
      docImp.subjectName
    })
    arr.length
  }
  //TODO: Implement!
  private def insertLessons(bytes: Array[Byte])  = {
    println(bytes)
    val arr = ImportJsonList.lessonList(bytes)
    arr.length
  }
  //TODO: Implement!
  private def insertQuestions(bytes: Array[Byte]) = {
    println(bytes)
    val arr = ImportJsonList.questionList(bytes)
    arr.length

  }
  //TODO: Implement!
  private def insertVideos(bytes: Array[Byte]) = {
    println(bytes)
    val arr = ImportJsonList.videoList(bytes)

    arr.length
  }
  //TODO: Implement!
  private def insertPresentation(bytes: Array[Byte]) = {
    println(bytes)
    val arr = ImportJsonList.presentationList(bytes)

    arr.length
  }
  //TODO: Implement!
  private def insertProblems(bytes: Array[Byte]) = {
    println(bytes)
    val arr = ImportJsonList.problemList(bytes)

    arr.length
  }

  private def insertCourses(bytes: Array[Byte]) = {
    println(bytes)
    val arr = ImportJsonList.courseList(bytes)
    arr.length
  }


  private def saveFileToDB(nameWithId:String, data:Array[Byte]): Unit = {
    val nameArr = nameWithId.split('.')
    if(nameArr.length > 1) {
      val idStr = nameArr.head
      val nameFile = nameArr.drop(1).mkString(".")
      val mime = nameArr.last.toLowerCase
      MongoDB.use(DefaultMongoIdentifier) {
        db =>
          val fs = new GridFS(db)
          val inputFile = fs.createFile(data)
          inputFile.setContentType(mime)
          inputFile.setFilename(nameFile)
          inputFile.setId(new ObjectId(idStr))
          inputFile.save

      }
    }
  }

}
