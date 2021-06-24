package eu.brosbit.opos.api

import com.mongodb.gridfs.GridFS
import com.mongodb.BasicDBObject

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import eu.brosbit.opos.lib.Zipper
import eu.brosbit.opos.model.User
import eu.brosbit.opos.model.edu.{Document, LessonCourse, QuizQuestion, Slide, SlideContent, Video}
import net.liftweb.json.JsonDSL._
import net.liftweb.common.{Box, Full}
import net.liftweb.http.{InMemoryResponse, LiftResponse, NotFoundResponse, StreamingResponse}
import net.liftweb.mongodb.{DefaultMongoIdentifier, MongoDB}
import org.bson.types.ObjectId


object Exports {

  ///not used
  def slides(what: String): Box[LiftResponse] = what match {
    case "slides" => ImportExportSlides.zip()
    case _ => Full(NotFoundResponse("Not found"))
  }

  def export(): Box[LiftResponse] = {
    val mime = "zip"
    val userBox = User.currentUser
    if(userBox.isEmpty || (userBox.openOrThrowException("Not possible").role.get != "n"  &&
      userBox.openOrThrowException("Not possible").role.get != "a"  &&
      userBox.openOrThrowException("Not possible").role.get == "d" ))
      Full(NotFoundResponse("You are not permitted!"))
    else {
      val data = createZip(userBox.openOrThrowException("Not possible"))
      val inputStream = new ByteArrayInputStream(data)
      if (inputStream.available() < 10) {
        Full(NotFoundResponse("Not found"))
      }
      else {
        Full(InMemoryResponse(data, ("Content-Type", "application/zip") :: ("Content-Disposition", "form-data; filename=\"export.zip\"") :: Nil, Nil, 200))
      }
    }
  }

  def jsonExportString(user: User): String = {
    val files = getImgsAndFiles(user)
    val presentations = getPresentations(user)
    s"""{"docs": ${getDocuments(user)}, "quests": ${getQuizQuestions(user)}, "slides": {${presentations}} },
       | "videos": ${getVideos(user)}, "files": ${files}}""".stripMargin

  }

  private val packager = new Zipper()

  object JsonFileNames extends Enumeration {
    val Documents:Value  = Value("documents.json")
    val Presentations:Value = Value("presentations.json")
    val Questions:Value = Value("questions.json")
    val Videos:Value = Value("videos.json")
    val Lessons:Value  = Value("lessons.json")
    val Files:Value = Value("files.json")
  }
//TODO: implement files to zip
  private def createZip(user:User): Array[Byte] = {
   val map =  jsonStringDataToMap(user)
    val fileMap = getImgAndFiles(user)
    //fileMap.foreach(m => println(m._1 + ": " + m._2.length))
    packager.toZipJsonAndBinary(map, fileMap)
    //map("dane").getBytes("UTF-8")
  }
  protected def jsonStringDataToMap(user: User):Map[String, String]= {
    Map(
      JsonFileNames.Documents.toString     -> (s""""${JsonFileNames.Documents.toString.split('.').head}":${getDocuments(user)}"""),
      JsonFileNames.Presentations.toString -> (s""""${JsonFileNames.Presentations.toString.split('.').head}":${getPresentations(user)}"""),
      JsonFileNames.Questions.toString     -> (s""""${JsonFileNames.Questions.toString.split('.').head}":${getQuizQuestions(user)}"""),
      JsonFileNames.Videos.toString        -> (s""""${JsonFileNames.Videos.toString.split('.').head}":${getVideos(user)}"""),
      JsonFileNames.Files.toString         -> (s""""${JsonFileNames.Files.toString.split('.').head}":${getImgsAndFiles(user)}"""),
      JsonFileNames.Lessons.toString       -> (s""""${JsonFileNames.Lessons.toString.split('.').head}":${getLessonCourses(user)}""")
    )
  }

  private def getDocuments(user: User) = {
    val docs = Document.findAll("authorId" -> user.id.get).map(d => d.strJson)
      .mkString(", ")
    "[" + docs + "]"
  }
  private def getQuizQuestions(user: User) = {
    val quizzes = QuizQuestion.findAll("authorId" -> user.id.get).map(q => q.strJson)
      .mkString(", ")
    "[" + quizzes + "]"
  }

  private def getPresentations(user:User) = {
    val slides = Slide.findAll("authorId" -> user.id.get)
    val slideContents = SlideContent.findAll("_id" -> {
      "$in" -> slides.map(_.slides.toString)
    })

      "{\"slidesHead\":[" + slides.map(s => s.strJson).mkString(", ") + "], " +
      "\"slidesContent\":[" + slideContents.map(s => s.strJson).mkString(", ") + "]}"
  }

  private def getVideos(user:User) = {
    val videos = Video.findAll("authorId" -> user.id.get).map(v => v.strJson).mkString(",<br/>\n")
    "[" + videos + "]"
  }
  private def getLessonCourses(user:User) = {
    val lessons = LessonCourse.findAll("authorId"->user.id.get).map(l => {l.strJson}).mkString("\n")
    "[" + lessons + "]"
  }

//only for test!
  private def getImgsAndFiles(user: User) = {
    val userId = user.id.get
    "[" + ImgFilesSearch.look(userId).map(i => "\"" + i + "\"").mkString(", ") + "]"
  }

  private def getImgAndFiles(user: User):Map[String, Array[Byte]] = {
    val ids = ImgFilesSearch.look(user.id.get)
    var filesMap:Map[String, Array[Byte]] = Map()
    val byteArrayOutputStream = new ByteArrayOutputStream()
    val query = new BasicDBObject("_id", new BasicDBObject("$in", ids))
    MongoDB.use(DefaultMongoIdentifier) {
      db =>
        val fs = new GridFS(db)
        fs.getFileList(query)
        val cursor = fs.getFileList()
        while (cursor.hasNext) {
          val gfsFile = fs.find(cursor.next()).get(0)
          val id = gfsFile.getId().asInstanceOf[ObjectId].toString
          val name = id + "." + gfsFile.getFilename
          //println(s"File: :::::  $name")
          gfsFile.writeTo(byteArrayOutputStream)
          filesMap += (name -> byteArrayOutputStream.toByteArray)
          byteArrayOutputStream.reset()
        }
    }
    filesMap
  }
}
