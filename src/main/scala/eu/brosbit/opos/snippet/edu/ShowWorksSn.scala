package eu.brosbit.opos.snippet.edu


import _root_.net.liftweb.util._
import Helpers._
import net.liftweb.json.JsonDSL._
import eu.brosbit.opos.model.edu.Work
import eu.brosbit.opos.lib.Formater
import java.util.Date
import eu.brosbit.opos.model.User

class ShowWorksSn extends BaseResourceSn {

  def subjectChoice():CssSel = super.subjectChoice("/educontent/works")

  def showWorks():CssSel = {
    val userId = User.currentUser.map(_.id.get).getOrElse(-1L)
    "tr"  #> Work.findAll(("teacherId" -> userId)~("subjectId" -> subjectId),
      "start" -> -1).sortWith(_.start > _.start).map(work => {
      ".col1 *" #> Formater.formatDate(new Date(work.start)) &
        ".col2 *" #> work.theme &
        ".col3 *" #> work.lessonTitle &
        ".col4 *" #> work.groupName &
        ".col5 *" #> (if(work.lastNews == 0) "Brak" else Formater.formatDate(new Date(work.lastNews))) &
        ".col6 *" #> <a href={"/educontent/showmessageswork/" + work._id.toString}
                        class="btn btn-small btn-info"><span class="glyphicon glyphicon-envelope"></span>Sprawdź</a> &
      ".col7 *" #> <a href={"/educontent/editwork/" + work._id.toString}
                      class="btn btn-small btn-success"><span class="glyphicon glyphicon-check"></span>Edytuj</a>
    })
  }
}
