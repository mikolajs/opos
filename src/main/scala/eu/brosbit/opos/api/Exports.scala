package eu.brosbit.opos.api

import java.io.ByteArrayInputStream

import eu.brosbit.opos.lib.ZipFilePackager
import eu.brosbit.opos.model.User
import eu.brosbit.opos.model.edu.{Document, LessonCourse, QuizQuestion, Slide, SlideContent, Video}
import net.liftweb.json.JsonDSL._
import net.liftweb.common.{Box, Full}
import net.liftweb.http.{InMemoryResponse, LiftResponse, NotFoundResponse, StreamingResponse}


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

  private val packager = ZipFilePackager()

  object JsonFileNames extends Enumeration {
    val Documents:Value  = Value("documents.json")
    val Presentations:Value = Value("presentations.json")
    val Questions:Value = Value("questions.json")
    val Videos:Value = Value("videos.json")
    val Lessons:Value  = Value("lessons.json")
  }

  private def createZip(user:User): Array[Byte] = {
    val map = jsonStringDataToMap(user)
    packager.toZip(map)
  }
  protected def jsonStringDataToMap(user: User):Map[String, String]= {
    Map(
      JsonFileNames.Documents.toString     -> ("\"documents\" :{" + getDocuments(user) + "}"),
      JsonFileNames.Presentations.toString -> ("\"presentations\" :{" + getPresentations(user) + "}"),
      JsonFileNames.Questions.toString     -> ("\"questions\":{" +  getQuizQuestions(user) +  "}"),
      JsonFileNames.Videos.toString        -> ("\"videos\":{" +  getVideos(user) + "}"),
      JsonFileNames.Lessons.toString       -> ("\"lessons\":{" + getLessonCourses(user) + "}")
    )
  }

  private def getDocuments(user: User) = {
    Document.findAll("authorId"->user.id.get).map(d => d.exportJsonString).mkString(", ")
  }
  private def getQuizQuestions(user: User) = {
    QuizQuestion.findAll("authorId" -> user.id.get).map(_.exportJsonString).mkString(", ")
  }

  private def getPresentations(user:User) = {
    val slides = Slide.findAll("authorId" -> user.id.get)
    val slideContents = SlideContent.findAll("_id" -> {"$in" -> slides.map(_.slides.toString)} )
    "\"heads\": {" + slides.map(_.exportJsonString).mkString(", ") + "}, " +
      "\"contents\": {" + slideContents.map(_.exportJsonString).mkString(",") + "}"
  }

  private def getVideos(user:User) = {
    Video.findAll("authorId"->user.id.get).map(_.exportJsonString).mkString("\n")
  }
  private def getLessonCourses(user:User) = {
    LessonCourse.findAll("authorId"->user.id.get).map(_.exportJsonString).mkString("\n")
  }

}
