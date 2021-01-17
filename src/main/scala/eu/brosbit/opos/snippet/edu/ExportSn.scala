package eu.brosbit.opos.snippet.edu


import eu.brosbit.opos.model.edu.{Document, LessonCourse, QuizQuestion, Slide, SlideContent, Video}
import net.liftweb.json.JsonDSL._
import net.liftweb.util.Helpers._
import net.liftweb.util.CssSel
import scala.xml.Unparsed

class ExportSn extends BaseResourceSn {

  private def getDocuments = {
    Document.findAll("authorId" -> user.id.get).map(d => d.exportJsonString).mkString("<br/>\n")
  }

  private def getQuizQuestions = {
    QuizQuestion.findAll("authorId" -> user.id.get).map(_.exportJsonString).mkString("<br/>\n")
  }

  private def getPresentations = {
    val slides = Slide.findAll("authorId" -> user.id.get)
    val slideContents = SlideContent.findAll("_id" -> {
      "$in" -> slides.map(_.slides.toString)
    })
    slides.map(_.exportJsonString).mkString("\n") +
      slideContents.map(_.exportJsonString).mkString("\n")
  }

  private def getVideos = {
    Video.findAll("authorId" -> user.id.get).map(_.exportJsonString).mkString("\n")
  }

  private def getLessonCourses = {
    LessonCourse.findAll("authorId" -> user.id.get).map(_.exportJsonString).mkString("\n")
  }

  def test: CssSel = {
    "#test1 *" #> Unparsed(getDocuments) &
      "#test2 *" #> Unparsed(getQuizQuestions) &
      "#test3 *" #> Unparsed(getPresentations) &
      "#test4 *" #> Unparsed(getVideos) &
      "#test5 *" #> Unparsed(getLessonCourses)
  }

}
