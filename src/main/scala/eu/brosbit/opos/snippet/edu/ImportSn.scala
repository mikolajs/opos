package eu.brosbit.opos.snippet.edu

import com.mongodb.BasicDBObject
import com.mongodb.gridfs.GridFS
import eu.brosbit.opos.api.{Exports, ImgFilesSearch}
import eu.brosbit.opos.lib.Zipper
import eu.brosbit.opos.model.User
import net.liftweb.common.{Box, Empty, Full}
import net.liftweb.http.{FileParamHolder, S, SHtml}
import net.liftweb.json.DefaultFormats
import net.liftweb.json.Serialization.read
import net.liftweb.mongodb.{DefaultMongoIdentifier, MongoDB}
import net.liftweb.util.CssSel
import net.liftweb.util.Helpers._
import org.bson.types.ObjectId

import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets

case class ElementsInZip(var docs:Int = 0, var presentations: Int = 0, var videos: Int = 0, var questions: Int = 0, var lessons: Int = 0,
                         var files: Int = 0, var problems: Int = 0)
case class ElementsJson(var docs:String = "", var presentations: String = "", var videos: String = "", var questions: String = "",
                        var lessons: String = "", var files: String = "", var problems: String = "")



class ImportSn {
  val user = User.currentUser.getOrElse(S.redirectTo("/"))
  private def mkImport(data: Array[Byte], eiz: ElementsInZip) = {
    val zipper = new Zipper()
    val mapFiles = zipper.fromZipJsonAndBinary(data)
    val mapKeys = mapFiles.keys
    println("mkIMPORT !!!!!!!!!!!!")
    val (jsonFiles, otherFiles) = mapFiles.partition(e => e.key.split('/').length == 2)
    jsonFiles.foreach( jsonFile => {
      val pathAr = jsonFile.key.split('/')
      if(pathAr.length == 2){
        pathAr.last match {
          case k if k == Exports.JsonFileNames.Documents.toString => insertDocuments(mapFiles(k), eiz)
          case k if k == Exports.JsonFileNames.Presentations.toString => insertPresentation(mapFiles(k), eiz)
          case k if k == Exports.JsonFileNames.Videos.toString => insertVideos(mapFiles(k), eiz)
          case k if k == Exports.JsonFileNames.Questions.toString => insertQuestions(mapFiles(k), eiz)
          case k if k == Exports.JsonFileNames.Lessons.toString => insertLessons(mapFiles(k), eiz)
          case k if k == Exports.JsonFileNames.Problems.toString => insertProblems(mapFiles(k), eiz)
          case k if k == Exports.JsonFileNames.Courses.toString => insertCourses(mapFiles(k), eiz)
          case k => println("ERROR: Not found key: " + k)
        }
      }
    })
    otherFiles.foreach(otherFile => {
      val pathAr = otherFile.key.split('/')
      if(pathAr.length == 3){
        saveFileToDB(pathAr.last, otherFile._2)
      }
    })
  }

  def importing: CssSel = {
    val elementsInZip = ElementsInZip()
    var error = ""
    def mkUpload(zipFile:FileParamHolder): Unit = {
      if(zipFile.fileName != "export.zip") error = "Błędna nazwa pliku"
      else mkImport(zipFile.file, elementsInZip)
    }

    def emptyFun(){}
    "#filezip" #> SHtml.fileUpload( zipFile => mkUpload(zipFile)) &
    "#docs *" #> elementsInZip.docs.toString &
    "#presentations *" #> elementsInZip.presentations.toString &
    "#videos *" #> elementsInZip.videos.toString &
    "#questions *" #> elementsInZip.questions.toString &
    "#lessons *" #> elementsInZip.lessons.toString &
    "#problems *" #> elementsInZip.problems.toString &
    "#files *" #> elementsInZip.files.toString &
    "#save"#> SHtml.submit("Wczytaj", () => emptyFun())
  }

  def test: CssSel = {
    val elementsJson = ElementsJson()
    var fileParamHolder:Box[FileParamHolder] = Empty
    var error = ""
    def save(): Unit = {
      if(fileParamHolder.isEmpty) {
        println("BRAK PLIKU ZIP!")
        return
      }
      val zipFile = fileParamHolder.openOrThrowException("Brak pliku")
      println("start import zip file: " + zipFile.fileName)
      if(zipFile.fileName != "export.zip") error = "Błędna nazwa pliku"
      else mkImportTest(zipFile.file, elementsJson)
      //println(elementsJson)
      S.notice(setInfo(elementsJson))
    }



    "#filezip" #> SHtml.fileUpload( zipFile => fileParamHolder = Full(zipFile)) &
      "#save"#> SHtml.submit("Wczytaj", () => save())
  }

  private def mkImportTest(data: Array[Byte], ej: ElementsJson): Unit = {
    val zipper = new Zipper()
    val mapFiles = zipper.fromZipJsonAndBinary(data).filterNot(_._1.take(10) == "dane/pliki")
    val mapKeys = mapFiles.keys
    def mkJsonString(a:Array[Byte]) = new String(a, 0, a.length, StandardCharsets.UTF_8)
    mapKeys.foreach(key => {
      val newKey = key.split('/').last
      newKey match {
        case k if k == Exports.JsonFileNames.Documents.toString => ej.docs = mkJsonString(mapFiles(key))
        case k if k == Exports.JsonFileNames.Presentations.toString => ej.presentations = mkJsonString(mapFiles(key))
        case k if k == Exports.JsonFileNames.Videos.toString => ej.videos = mkJsonString(mapFiles(key))
        case k if k == Exports.JsonFileNames.Questions.toString => ej.questions = mkJsonString(mapFiles(key))
        case k if k == Exports.JsonFileNames.Lessons.toString => ej.lessons = mkJsonString(mapFiles(key))
        case k if k == Exports.JsonFileNames.Problems.toString => ej.problems = mkJsonString(mapFiles(key))
        case _ => Unit
      }
    })
   // println(ej)
  }
  private def setInfo(json: ElementsJson): String = {
    s""" {${json.docs}, ${json.presentations}, ${json.videos},  ${json.questions}, ${json.lessons}, ${json.files}"""
  }
//TODO: Implement!
  private def insertDocuments(bytes: Array[Byte], zip: ElementsInZip): Unit = {
    println(bytes)

  }
  //TODO: Implement!
  private def insertLessons(bytes: Array[Byte], zip: ElementsInZip): Unit ={
    println(bytes)

  }
  //TODO: Implement!
  private def insertQuestions(bytes: Array[Byte], zip: ElementsInZip) = {
    println(bytes)

  }
  //TODO: Implement!
  private def insertVideos(bytes: Array[Byte], zip: ElementsInZip): Unit = {
    println(bytes)

  }
  //TODO: Implement!
  private def insertPresentation(bytes: Array[Byte], zip: ElementsInZip): Unit = {
    println(bytes)

  }
  //TODO: Implement!
  private def insertProblems(bytes: Array[Byte], zip: ElementsInZip) = {
    println(bytes)

  }

  private def insertCourses(bytes: Array[Byte], zip:ElementsInZip) = {
    println(bytes)
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
