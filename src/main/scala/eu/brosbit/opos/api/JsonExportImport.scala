package eu.brosbit.opos.api

import eu.brosbit.opos.model.edu.LessonContent

import scala.util.parsing.json.JSONFormat.quoteString


object JsonExportImport {
  import net.liftweb.json.Serialization.read
  import net.liftweb.json.Serialization.write
  import net.liftweb.json.DefaultFormats

  import net.liftweb.json._

  implicit val formats = DefaultFormats

  implicit def formatLessonContent(jsStr:String) = parse(jsStr).extract[LessonContent]

  case class DocumentsImport(_id:String, title:String, descript:String, subjectName:String, department:String,
                             content:String, lev:Int){
    def toJson = write(this)
  }

  case class LessonImport(_id:String, title:String, descript:String, chapter:String, courseId:String, contents:List[LessonContent], nr:Int){
    def toJson = write(this)
  }

  case class QuestionImport(_id:String, dificult:Int, lev:Int, subjectName:String, info: String, question: String,
                            department: String, answers:List[String], fake:List[String], hint:String){
    def toJson = write(this)
  }

  case class VideoImport(_id:String, title:String, descript:String, subjectName:String, department:String, link:String,
                         lev:Int, onServer:Boolean, mime:String, oldPath:String){
    def toJson = write(this)
  }

  case class PresentationImport(_id:String, title:String, descript:String, subjectName:String, department: String,
                                slides:String, lev: Int){
    def toJson = write(this)
  }

  case class ProblemImport(_id: String, title:String, description:String, info: String, inputs: String,
                            expectedOutputs: String){
    def toJson = write(this)
  }

  case class CourseImport(_id: String, title: String, chapters: List[String], subjectName: String,
                          descript: String, img: String){
    def toJson = write(this)
  }

  def fromJsonDocument(jsStr: String) = {
    val json = parse(jsStr)
    json.extract[DocumentsImport]
  }
  def fromJsonLesson(jsStr: String) = {
    val json = parse(jsStr)
    json.extract[LessonImport]
  }
  def fromJsonQuestion(jsStr: String) = {
    val json = parse(jsStr)
    json.extract[QuestionImport]
  }
  def fromJsonVideo(jsStr: String) = {
    val json = parse(jsStr)
    json.extract[VideoImport]
  }
  def fromJsonPresentation(jsStr: String) = {
    val json = parse(jsStr)
      json.extract[PresentationImport]
  }
  def fromJsonProblem(jsStr: String) = parse(jsStr).extract[ProblemImport]

  def fromJsonCourse(jsStr: String) = parse(jsStr).extract[CourseImport]

  def fromJson[A : Manifest](jsStr: String) = parse(jsStr).extract[A]
}
