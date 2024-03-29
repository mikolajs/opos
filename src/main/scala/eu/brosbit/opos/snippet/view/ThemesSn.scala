package eu.brosbit.opos.snippet.view

  import java.util.Date
  import eu.brosbit.opos.lib.Formater
  import eu.brosbit.opos.model.edu.{Groups, Work, WorkAnswer}
  import eu.brosbit.opos.model.SubjectName
  import net.liftweb.http.S
  import net.liftweb.util.Helpers._
  import net.liftweb.json.JsonDSL._
  import net.liftweb.util.CssSel

  class ThemesSn extends BaseSnippet {
    private val idSub = S.param("idS").map(id => tryo(id.toInt).getOrElse(-1)).openOr(-1)
    private val subjects = SubjectName.findAll()
    private val subject = subjects.find(s => idSub == s.id.get) match {
      case Some(sub) => sub
      case _ => subjects.headOption.getOrElse(SubjectName.create)
    }
    private val groups = Groups.findAll.filter(gr => gr.students.exists(s => s.id == user.id.get)).map("_" + _._id)

    def showThemes():CssSel = {
      val works = Work.findAll(("groupId"-> ("$in" -> groups)) ~ ("subjectId" -> subject.id.get))
          .sortWith((lt1, lt2) => lt1.start > lt2.start)
      val answers = WorkAnswer.findAll("authorId"->user.id.get)
      "tr" #> works.map(w => {
        ".workId *" #> w._id.toString &
          ".theme *" #> w.lessonTitle &
          ".course *" #> <a href={"/view/course/" + w.courseId.toString + "?l=" + w.lessonId.toString} target="_blank">LINK</a> &
          ".lessonDate *" #> Formater.formatDate(new Date(w.start)) &
          ".changes *" #> Formater.formatDate(new Date(w.lastNews)) &
          ".open * " #>  <a href={"/view/showmessageswork/" + w._id.toString}> <span class="btn btn-info" >
            <span class="glyphicon glyphicon-envelope"></span> Otwórz</span></a>
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
