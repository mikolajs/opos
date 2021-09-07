package eu.brosbit.opos.snippet.view

  import java.util.Date

  import eu.brosbit.opos.lib.Formater
  import eu.brosbit.opos.model.edu.{Work, WorkAnswer}
  import eu.brosbit.opos.model.{LessonThemes, SubjectName}
  import net.liftweb.http.S
  import net.liftweb.util.Helpers._
  import net.liftweb.json.JsonDSL._
  import net.liftweb.util.CssSel

  class ThemesSn extends BaseSnippet {
    private val idSub = S.param("idS").map(id => tryo(id.toInt).getOrElse(-1)).openOr(-1)
    private val classId = user.classId.get
    private val subjects = SubjectName.findAll()
    private val subject = subjects.find(s => idSub == s.id.get) match {
      case Some(sub) => sub
      case _ => subjects.headOption.getOrElse(SubjectName.create)
    }

    def showThemes():CssSel = {
      val works = Work.findAll("classId"-> user.classId.get)
          .sortWith((lt1, lt2) => lt1.start > lt2.start)
      val answers = WorkAnswer.findAll("authorId"->user.id.get)
      "tr" #> works.map(w => {
        val change = answers.find(a => a.work.toString == w._id.toString)
          .map(wa => wa.teacherChanged).getOrElse(false)
        ".workId *" #> w._id.toString &
          ".theme *" #> w.lessonTitle &
          ".lessonDate *" #> Formater.formatDate(new Date(w.start)) &
          ".changes *" #> (if(change) <span class="isRed"></span> else <span class="notRed"></span>) &
          ".open * " #>  <a href={"/view/showwork/" + w._id.toString}> <span class="btn btn-info" >
            <span class="glyphicon glyphicon-edit"></span> Otwórz</span></a>
      })

    }

    def showSubjects():CssSel = {
      "button" #> subjects.map(s => {
        val setClass = if (subject.id.get == s.id.get) "btn btn-info" else "btn btn-default"
        <a href={"/view/themes/" + s.id.toString} class ={setClass}>{s.name}</a>
      })
    }

    def chosenSubject():CssSel = {
      "span *" #> subject.name.get
    }

}
