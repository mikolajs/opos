package eu.brosbit.opos.snippet.edu


import _root_.net.liftweb._
import http.{S, SHtml}
import util._
import Helpers._
import net.liftweb.common.Full
import eu.brosbit.opos.model.User
import eu.brosbit.opos.snippet.BaseShowCourseSn

class ShowCourseSn extends BaseShowCourseSn {

  override val basePath = "/educontent/course/"

  val user = User.currentUser match {
    case Full(user) => user
    case _ => S.redirectTo("/login")
  }

  def show() = {
    if (course.authorId != user.id.get) S.redirectTo("/educontent/index")

    if (course.title != "") {
      "#subjectListLinks a" #> super.createLessonList &
        "#addLessonButton [href]" #> ("/educontent/editlesson/0?c=" + course._id.toString()) &
        "#courseInfo" #> <div class="alert alert-success">
          <h2>
            {course.title}
          </h2> <p>
            {course.descript}
          </p>
        </div> &
        ".content *" #> super.showAsDocument(currentLesson, true)
    } else ".main *" #> <h1>Nie ma takiego kursu lub brak lekcji</h1>
  }


  def sortedChapters() = {
    "li" #> course.chapters.map(ch => <li class="list-group-item">
      {ch}
    </li>)
  }

  def sortChapters() = {
    var sorted = ""
    def delete() {
      val allChapters = lessons.map(l => l.chapter).distinct
      course.chapters = course.chapters.filter(ch => allChapters.exists(all => all == ch))
      course.save
    }
    def save() {
      //println("SORTED: " + sorted );
      val chaps = sorted.split("\\|\\|").map(s => s.trim).filter(s => s.length > 2).toList
      //println("[AppInfo:::: sort " + chaps.length + " : " + chaps.mkString("||"))
      if (chaps.length == course.chapters.length) {
        course.chapters = chaps
        course.save
      }
    }
    "#sortedChaptersData" #> SHtml.text(sorted, sorted = _, "style" -> "display:none;") &
      "#saveSort" #> SHtml.submit("Zapisz", save) &
      "#deleteNotUsed" #> SHtml.submit("Czyść puste", delete)
  }

  def slideData = {

  }


}
