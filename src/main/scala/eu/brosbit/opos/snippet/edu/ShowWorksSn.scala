package eu.brosbit.opos.snippet.edu


import _root_.net.liftweb.util._
import Helpers._
import net.liftweb.json.JsonDSL._
import net.liftweb.http.{S, SHtml}
import eu.brosbit.opos.model.edu.{ LessonWork, LessonWorkAnswer}
import eu.brosbit.opos.lib.Formater
import java.util.Date

import eu.brosbit.opos.model.{ClassModel, User}
import net.liftweb.common.Full

class ShowWorksSn extends BaseResourceSn {

  //TODO: change class to serve only works.html
//  private val classes = ClassModel.findAll().map(cl => (cl.id.toString, cl.classString()))
//  private var classId = S.param("c").getOrElse(classes.headOption.map(_._1).getOrElse("0"))
  private val subjects = subjectTeach.map(sub => (sub.id.toString, sub.name))
  private var subjectChoiceStr:String = subjectId.toString

  def subjectChoice() = super.subjectChoice("/educontent/works")



  def showWorks() = {
    val userId = User.currentUser.map(_.id.get).getOrElse(-1L)
    "tr"  #> LessonWork.findAll(("teacherId" -> userId)~("subjectId" -> subjectId),
      ("start" -> -1)).map(work => {
      ".col1 *" #> work.lessonTitle &
        ".col2 *" #> work.className &
        ".col3 *" #> Formater.formatTime(new Date(work.start)) &
        ".col4 *" #> Formater.formatTime(new Date(work.end)) &
        ".col5 *" #> <a href={"/educontent/showlessonworks/" + work._id.toString}
                        class="btn btn-small btn-success"><span class="glyphicon glyphicon-check"></span>Edytuj</a> &
      ".col6 *" #> <a href={"/educontent/editwork/" + work._id.toString}
                      class="btn btn-small btn-success"><span class="glyphicon glyphicon-check"></span>Edytuj</a>

    })
  }

//  def selection() = {
//
//      "#subjectWork" #> SHtml.select(subjects, Full(subjectChoiceStr), subjectChoiceStr= _) &
////      "#classWork" #> SHtml.select(classes, Full(classId), classId = _ ) &
//      "#choiceWork" #> SHtml.submit("Wybierz", () =>
//        S.redirectTo(s"/educontent/works?s=$subjectChoiceStr"))
//  }


}
