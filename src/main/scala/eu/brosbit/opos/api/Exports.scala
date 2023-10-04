package eu.brosbit.opos.api

import com.mongodb.gridfs.GridFS
import com.mongodb.BasicDBObject
import eu.brosbit.opos.api.JsonExportImport.{CourseImport, DocumentsImport, LessonImport, PresentationImport, ProblemImport, QuestionImport, VideoImport}

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import eu.brosbit.opos.lib.Zipper
import eu.brosbit.opos.model.User
import eu.brosbit.opos.model.edu.{Course, Document, LessonCourse, Presentation, QuizQuestion, Slide, SlideContent, Video}
import eu.brosbit.opos.model.TestProblem
import net.liftweb.json.JsonDSL._
import net.liftweb.common.{Box, Full}
import net.liftweb.http.{InMemoryResponse, LiftResponse, NotFoundResponse, StreamingResponse}
import net.liftweb.mongodb.{DefaultMongoIdentifier, MongoDB}
import org.bson.types.ObjectId

//import java.nio.file.{Files, Paths}
/*It should be export only for Administrator!!!*/

object Exports {

  ///not used
 /* def slides(what: String): Box[LiftResponse] = what match {
    case "slides" => ImportExportSlides.zip()
    case _ => Full(NotFoundResponse("Not found"))
  } */

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

  private val packager = new Zipper()

  object JsonFileNames extends Enumeration {
    val Documents:Value  = Value("documents.json")
    val Presentations:Value = Value("presentations.json")
    val Questions:Value = Value("questions.json")
    val Videos:Value = Value("videos.json")
    val Lessons:Value  = Value("lessons.json")
    val Problems:Value = Value("problems.json")
    val Courses:Value = Value("courses.json")
  }
//TODO: implement files to zip
  private def createZip(user:User): Array[Byte] = {
   val map =  jsonStringDataToMap(user)
    val fileMap = getImgAndFiles(user)
    //fileMap.foreach(m => println(m._1 + ": " + m._2.length))
    packager.toZipJsonAndBinary(map, fileMap)
    //map("dane").getBytes("UTF-8")
  }
  private def jsonStringDataToMap(user: User):Map[String, String]= {
    Map(
      JsonFileNames.Documents.toString     -> getDocuments(user),
      JsonFileNames.Presentations.toString -> getPresentations(user),
      JsonFileNames.Questions.toString     -> getQuizQuestions(user),
      JsonFileNames.Videos.toString        -> getVideos(user),
      JsonFileNames.Lessons.toString       -> getLessonCourses(user),
      JsonFileNames.Problems.toString       -> getProblems(user),
      JsonFileNames.Courses.toString       -> getCourses(user)
    )
  }

  private def getDocuments(user: User) = {
    val docs = Document.findAll("authorId" -> user.id.get).map(d => {
      DocumentsImport(d._id.toString, d.title, d.descript, d.subjectName, d.department, d.content, d.lev).toJson
    }).mkString(", ")
    "[" + docs + "]"
  }
  private def getQuizQuestions(user: User) = {
    val quizzes = QuizQuestion.findAll("authorId" -> user.id.get).map(q => {
      QuestionImport(q._id.toString, q.dificult, q.lev, q.subjectName, q.info, q.question, q.department, q.answers, q.fake, q.hint).toJson
    }).mkString(", ")
    "[" + quizzes + "]"
  }

  //TO delete after change to Presentation
  private def getSlides(user:User) = {
    val slides = Slide.findAll("authorId" -> user.id.get)
    val slideContents = SlideContent.findAll("_id" -> {
      "$in" -> slides.map(_.slides.toString)
    })

      "{\"slidesHead\":[" + slides.map(s => s.strJson).mkString(", ") + "], " +
      "\"slidesContent\":[" + slideContents.map(s => s.strJson).mkString(", ") + "]}"
  }

  private def getPresentations(user: User) = {
    val presentations = Presentation.findAll("authorId"->user.id.get).map(p => {
      PresentationImport(p._id.toString, p.title, p.descript, p.subjectName, p.department, p.slides, p.lev).toJson
    }).mkString(", ")
    "[" + presentations + "]"
  }

  private def getVideos(user:User) = {
    val videos = Video.findAll("authorId" -> user.id.get).map(v => {
      VideoImport(v._id.toString, v.title, v.descript, v.subjectName, v.department, v.link, v.lev, v.onServer, v.mime, v.oldPath).toJson
    }).mkString(", ")
    "[" + videos + "]"
  }
  private def getLessonCourses(user:User) = {
    val lessons = LessonCourse.findAll("authorId"->user.id.get).map(l => {
      LessonImport(l._id.toString, l.title, l.descript, l.chapter, l.courseId.toString, l.contents, l.nr).toJson
    }).mkString(", ")
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
    //testSaveFiles(filesMap)
    filesMap
  }

  private def getProblems(user: User) = {
    val userId = user.id.get
    val problems = TestProblem.findAll("author"->userId).map(tp =>
      {
        ProblemImport(tp._id.toString, tp.title, tp.description, tp.info, tp.inputs, tp.expectedOutputs).toJson
      }).mkString(", ")
    "[" + problems + "]"
  }

  private def getCourses(user: User) = {
    val userId = user.id.get
    val courses = Course.findAll("authorId"->userId).map(c => {
      CourseImport(c._id.toString, c.title, c.chapters, c.subjectName, c.descript, c.img).toJson
    }).mkString(", ")
    "[" + courses + "]"
  }



}
