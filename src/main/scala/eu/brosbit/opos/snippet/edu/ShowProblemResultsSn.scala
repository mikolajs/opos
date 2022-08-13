package eu.brosbit.opos.snippet.edu

import eu.brosbit.opos.lib.Formater
import eu.brosbit.opos.model.edu.Groups
import eu.brosbit.opos.model.{TestProblem, TestProblemTry, User}
import net.liftweb.common.Full
import net.liftweb.http.{S, SHtml}
import net.liftweb.json.JsonDSL._
import net.liftweb.util.CssSel
import net.liftweb.util.Helpers._

import java.util.Date
import scala.xml.Unparsed

class ShowProblemResultsSn {
  var problemId = S.param("problemId").getOrElse("0")
  var groupId = S.param("groupId").getOrElse("0")
  var user = User.currentUser.getOrElse(S.redirectTo("/login"))
  val allGroups = Groups.findAll
  val groupsSelect = allGroups.map(g => (g._id.toString -> g.name))
  if(allGroups.length == 0) S.redirectTo("/educontent/groups")
  val group = if(groupId == "0") allGroups.head else allGroups.find(_._id.toString == groupId).getOrElse(allGroups.head)
  //println("GROUP: " + group.name)
  val problem = if(problemId == "0") TestProblem.create else TestProblem.find(problemId).getOrElse(TestProblem.create)
  val allIdPupils = group.students.map(_.id)
  //println("STUDENTS: " + allIdPupils.mkString(", "))
  val aTrays = TestProblemTry.findAll(("problem" -> problem._id.toString)~("checked"->true)~("author" -> ("$in" -> allIdPupils)) )
      .sortWith((t1, t2) => t1.aDate > t2.aDate)
  //println("TRAYS: " + aTrays.length)
  def show:CssSel = {
    val titleP = problem.title
    //var infoP = problem.info
    val descriptionP = problem.description
    def saveProblem() {
      problem.description = descriptionP
      problem.title = titleP
    }
    "#titleProblem *" #> titleP  &
    "#descriptionProblem *" #> Unparsed(descriptionP) &
    "#takeClass" #> SHtml.select(groupsSelect, Full(groupId) , (s) =>"onclick=showProblemResults.openGroup()")
  }

  def showJson:CssSel = {
    "#jsonData *" #> mkTestJson
  }

  private def mkTestJson = "[" + aTrays.map(_.jsonStr).mkString(",") + "]"

  def showLastTests: CssSel = {
    //println("START ShowLastTests!!!!!!!")
    ".checkTr *" #> aTrays.map(aTray => {
      //println("aTray: " + aTray.outputs)
      ".col1 [id]" #> ((if(aTray.good) "G" else "B") + aTray._id.toString) &
        ".col1 *" #> aTray.authorInfo &
        ".col2 *" #> Formater.formatTimeForSort(new Date(aTray.aDate)) &
        ".col3 *" #> (if (!aTray.checked) <p>W trakcie</p> else mkResultInfo(aTray)) &
        ".col4 .code *" #> aTray.code
    })
  }

  private def mkResultInfo(aTray: TestProblemTry) = {
    if (aTray.good) <p>
      <span style="color:green;font-size:1.5em;">
        &#x2714;
      </span>{aTray.outputs}
    </p>
    else <p>
      <span style="color:red;font-size:1.5em;">
        &#x2718;
      </span>{aTray.outputs}
    </p>
  }
}
