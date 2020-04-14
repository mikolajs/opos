package eu.brosbit.opos.snippet.pub



import net.liftweb.util.Helpers._

import eu.brosbit.opos.snippet.BaseShowCourseSn

class ShowCourseSn extends BaseShowCourseSn {

  override val basePath = "/public/course/"
//controla czy można oglądać kurs
  def show() = {

    if (course.title != "" && course.pub) {
      "#subjectListLinks a" #> createLessonList &
        "#courseInfo" #> <div class="alert alert-success">
          <h2>
            {course.title}
          </h2> <p>
            {course.descript}
          </p>
        </div> &
        ".content *" #> this.showAsDocument(currentLesson, false)
    } else ".main *" #> <h1>Nie ma takiego kursu lub brak lekcji udostępnionej publicznie</h1>
  }



}