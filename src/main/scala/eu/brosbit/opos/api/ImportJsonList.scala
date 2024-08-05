package eu.brosbit.opos.api

case class ImportList(d: List[JsonExportImport.DocumentsImport])

import eu.brosbit.opos.snippet.edu.ElementsInZip
import net.liftweb.json.Serialization.write
import net.liftweb.json.DefaultFormats
import net.liftweb.json._
import net.liftweb.util.Helpers.pairToUnprefixed

import java.nio.charset.StandardCharsets
import scala.xml.MetaData._

case class JsonImportObject(var doc:List[JsonExportImport.DocumentsImport] ,var quest:List[JsonExportImport.QuestionImport] ,
                            var pres:List[JsonExportImport.PresentationImport] , var vid:List[JsonExportImport.VideoImport],
                            var less:List[JsonExportImport.LessonImport], var cour:List[JsonExportImport.CourseImport])

object ImportJsonList {
  implicit val formats = DefaultFormats

  def convertJsonToObjects(m:Map[String, Array[Byte]]):JsonImportObject = {
    val jio = JsonImportObject(Nil, Nil, Nil, Nil, Nil, Nil)
    m.foreach(jsonFile => {
      jsonFile.key match {
        case k if k == Exports.JsonFileNames.Documents.toString => jio.doc = documentList(jsonFile._2)
        case k if k == Exports.JsonFileNames.Presentations.toString => jio.pres = presentationList(jsonFile._2)
        case k if k == Exports.JsonFileNames.Videos.toString => jio.vid = videoList(jsonFile._2)
        case k if k == Exports.JsonFileNames.Questions.toString => jio.quest = questionList(jsonFile._2)
        case k if k == Exports.JsonFileNames.Courses.toString => jio.cour = courseList(jsonFile._2)
        case k if k == Exports.JsonFileNames.Lessons.toString => jio.less = lessonList(jsonFile._2)
        case _ =>
      }
    })
    jio
  }

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


  def getSubjects(jsonFiles:Map[String, Array[Byte]], eiz:ElementsInZip): Seq[String] = {
    jsonFiles.flatMap(jsonFile => {
      val pathAr = jsonFile.key.split('/')
      pathAr.last match {
        case k if k == Exports.JsonFileNames.Documents.toString => getSubjectsDoc(documentList(jsonFile._2), eiz)
        case k if k == Exports.JsonFileNames.Presentations.toString => getSubjectsPre(presentationList(jsonFile._2), eiz)
        case k if k == Exports.JsonFileNames.Videos.toString => getSubjectsVid(videoList(jsonFile._2), eiz)
        case k if k == Exports.JsonFileNames.Questions.toString => getSubjectsQuest(questionList(jsonFile._2), eiz)
        case k if k == Exports.JsonFileNames.Courses.toString => getSubjectsCourse(courseList(jsonFile._2), eiz)
        //case k if k == Exports.JsonFileNames.Problems.toString => {
          //getProblemsSize(problemList(jsonFile._2), eiz)
          Nil
        //}
        case k if k == Exports.JsonFileNames.Lessons.toString => {
          setLessonsSize(lessonList(jsonFile._2), eiz)
          Nil
        }
        case _ => List[String]()
      }
    }).toList.distinct
  }
//List of used subject names in types documents to know
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
  private def getSubjectsCourse(list:List[JsonExportImport.CourseImport], zip: ElementsInZip) = {
    zip.courses = list.size
    list.map(_.subjectName)
  }

  //private def getProblemsSize(imports: List[JsonExportImport.ProblemImport], zip: ElementsInZip): Unit = zip.problems = imports.length

  private def setLessonsSize(imports: List[JsonExportImport.LessonImport], zip: ElementsInZip): Unit = zip.lessons = imports.length

}
