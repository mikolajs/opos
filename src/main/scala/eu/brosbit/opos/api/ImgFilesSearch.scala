package eu.brosbit.opos.api

import eu.brosbit.opos.model.Lesson
import eu.brosbit.opos.model.edu.{Document, LessonCourse, QuizQuestion, Slide, SlideContent}
import net.liftweb.json.JsonDSL._
import net.liftweb.util.Helpers._

object ImgFilesSearch {
  private val regexImg = raw"""src=\"/img/[0-9A-Fa-f]{24}\.\w{3,4}""".r
  private val regexFile = raw"""href=\"/file/[0-9A-Fa-f]{24}\.\w{1,4}""".r

  //TODO: implement search for garbage collector
  def look(userId: Long, forExport: Boolean = true):List[String] = {
    val ids = questions(userId) ++ articles(userId) ++ slides(userId) ++ lessonsNotes(userId)
    if(forExport) ids else  Nil
  }

  private def questions(authorId: Long) = {
    val quests = QuizQuestion.findAll("authorId" -> authorId).map(_.question)
    quests.flatMap(q => regexImg.findAllIn(q)).map(_.split('/').last) ++
      quests.flatMap(q => regexFile.findAllIn(q)).map(_.split('/').last)
  }
  private def articles(authorId: Long) = {
    val contents = Document.findAll("authorId" -> authorId).map(_.content.toString)
    contents.flatMap(q => regexImg.findAllIn(q)).map(_.split('/').last) ++
      contents.flatMap(q => regexFile.findAllIn(q)).map(_.split('/').last)
  }
  private def slides(authorId: Long) = {
    val slideHeads = Slide.findAll("authorId" -> authorId).map(_.slides.toString)
    val contents = SlideContent.findAll("_id" -> ("$in" -> slideHeads)).map(_.slides)
    contents.flatMap(q => regexImg.findAllIn(q)).map(_.split('/').last) ++
      contents.flatMap(q => regexFile.findAllIn(q)).map(_.split('/').last)
  }

  private def lessonsNotes(authorId: Long) = {
    val lessons = LessonCourse.findAll("authorId" -> authorId).flatMap(_.contents).filter(_.what == "n").map(_.descript)
    lessons.flatMap(q => regexFile.findAllIn(q)).map(_.split('/').last) ++
      lessons.flatMap(q => regexImg.findAllIn(q)).map(_.split('/').last)
  }

}
