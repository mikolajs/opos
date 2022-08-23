package eu.brosbit.opos.api

case class ImportList(d: List[JsonExportImport.DocumentsImport])

import eu.brosbit.opos.snippet.edu.ElementsInZip
import net.liftweb.json.Serialization.write
import net.liftweb.json.DefaultFormats
import net.liftweb.json._
import net.liftweb.util.Helpers.pairToUnprefixed

import java.nio.charset.StandardCharsets
import scala.xml.MetaData._




object ImportJsonList {
  implicit val formats = DefaultFormats

  def documentList(bytes:Array[Byte]): List[JsonExportImport.DocumentsImport]  = {
    implicit def jsonElem (jsStr: String) = JsonExportImport.fromJsonDocument (jsStr)
    val json = parse(new String(bytes, StandardCharsets.UTF_8))
    json.extract[List[JsonExportImport.DocumentsImport]]
  }

  def questionList(bytes: Array[Byte]): List[JsonExportImport.QuestionImport] = {
    implicit def jsonElem(jsStr: String) = JsonExportImport.fromJsonQuestion(jsStr)
    val json = parse(new String(bytes, StandardCharsets.UTF_8))
    json.extract[List[JsonExportImport.QuestionImport]]
  }

  def presentationList(bytes: Array[Byte]): List[JsonExportImport.PresentationImport] = {
    implicit def jsonElem(jsStr: String) = JsonExportImport.fromJsonPresentation(jsStr)
    val json = parse(new String(bytes, StandardCharsets.UTF_8))
   json.extract[List[JsonExportImport.PresentationImport]]
  }

  def videoList(bytes: Array[Byte]): List[JsonExportImport.VideoImport] = {
    implicit def jsonElem(jsStr: String) = JsonExportImport.fromJsonVideo(jsStr)
    val json = parse(new String(bytes, StandardCharsets.UTF_8))
    json.extract[List[JsonExportImport.VideoImport]]
  }

  def questList(bytes: Array[Byte]): List[JsonExportImport.QuestionImport] = {
    implicit def jsonElem(jsStr: String) = JsonExportImport.fromJsonVideo(jsStr)
    val json = parse(new String(bytes, StandardCharsets.UTF_8))
    json.extract[List[JsonExportImport.QuestionImport]]
  }

  def courseList(bytes: Array[Byte]): List[JsonExportImport.CourseImport] = {
    implicit def jsonElem(jsStr: String) = JsonExportImport.fromJsonCourse(jsStr)
    val json = parse(new String(bytes, StandardCharsets.UTF_8))
    json.extract[List[JsonExportImport.CourseImport]]
  }

  def problemList(bytes: Array[Byte]): List[JsonExportImport.ProblemImport] = {
    implicit def jsonElem(jsStr: String) = JsonExportImport.fromJsonProblem(jsStr)
    val json = parse(new String(bytes, StandardCharsets.UTF_8))
    json.extract[List[JsonExportImport.ProblemImport]]
  }

  def lessonList(bytes: Array[Byte]): List[JsonExportImport.LessonImport] = {
    implicit def jsonElem(jsStr: String) = JsonExportImport.fromJsonLesson(jsStr)
    val json = parse(new String(bytes, StandardCharsets.UTF_8))
    json.extract[List[JsonExportImport.LessonImport]]
  }


  def getSubjects(jsonFiles:Map[String, Array[Byte]], eiz:ElementsInZip) = {
    jsonFiles.filter(jsonFile => jsonFile.key.split('/').length == 2).map(jsonFile => {
      val pathAr = jsonFile.key.split('/')
      pathAr.last match {
          case k if k == Exports.JsonFileNames.Documents.toString => getSubjectsDoc(documentList(jsonFile._2), eiz)
          case k if k == Exports.JsonFileNames.Presentations.toString => getSubjectsPre(presentationList(jsonFile._2), eiz)
          case k if k == Exports.JsonFileNames.Videos.toString => getSubjectsVid(videoList(jsonFile._2), eiz)
          case k if k == Exports.JsonFileNames.Questions.toString => getSubjectsQuest(questList(jsonFile._2), eiz)
          case k if k == Exports.JsonFileNames.Courses.toString => getSubjectsCourse(courseList(jsonFile._2))
          case k if k == Exports.JsonFileNames.Problems.toString => {
            getProblemsSize(problemList(jsonFile._2), eiz)
            Nil
          }
          case k if k == Exports.JsonFileNames.Lessons.toString => {
            getLessonsSize(lessonList(jsonFile._2), eiz)
            Nil
          }
          case _ => List[String]()
      }
    }).flatten.toList.distinct
  }

  private def getSubjectsDoc(list:List[JsonExportImport.DocumentsImport], eiz:ElementsInZip) = {
    eiz.docs = list.length
    list.map(_.subjectName)
  }
  private def getSubjectsPre(list:List[JsonExportImport.PresentationImport], eiz:ElementsInZip) = {
    eiz.presentations = list.length
    list.map(_.subjectName)
  }
  private def getSubjectsVid(list:List[JsonExportImport.VideoImport], eiz:ElementsInZip) = {
    eiz.videos = list.size
    list.map(_.subjectName)
  }
  private def getSubjectsQuest(list:List[JsonExportImport.QuestionImport], eiz:ElementsInZip) = {
    eiz.questions = list.length
    list.map(_.subjectName)
  }
  private def getSubjectsCourse(list:List[JsonExportImport.CourseImport]) = list.map(_.subjectName)

  private def getProblemsSize(imports: List[JsonExportImport.ProblemImport], zip: ElementsInZip) = zip.problems = imports.length

  private def getLessonsSize(imports: List[JsonExportImport.LessonImport], zip: ElementsInZip) = zip.lessons = imports.length

}
