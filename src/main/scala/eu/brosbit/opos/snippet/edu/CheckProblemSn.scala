package eu.brosbit.opos.snippet.edu

import eu.brosbit.opos.lib.{Formater, TestProblemRunner}
import eu.brosbit.opos.model.{TestProblem, TestProblemTry, User}
import net.liftweb.util.CssSel
import net.liftweb.util.Helpers._
import net.liftweb.http.{S, SHtml}
import net.liftweb.json.JsonDSL._

import java.util.Date
import java.util.zip.DataFormatException
import scala.xml.{Text, Unparsed}

class CheckProblemSn {
  var id = S.param("id").getOrElse("0")
  var user = User.currentUser.getOrElse(S.redirectTo("/login"))
  println("im a user " + user.id.get)
  val problem = if(id == "0") TestProblem.create else TestProblem.find(id).getOrElse(TestProblem.create)
  if(problem.author != 0 && problem.author != user.id.get ) S.redirectTo("/educontent/problems")
  else if(problem.author == 0L) problem.author = user.id.get
  val aTrays = TestProblemTry.findAll(("problem" -> problem._id.toString)~("author" -> user.id.get) ).sortWith((t1, t2) => t1.aDate > t2.aDate)

  def show:CssSel = {
    val titleP = problem.title
    //var infoP = problem.info
    val descriptionP = problem.description
    def saveProblem() {
      problem.description = descriptionP
      problem.title = titleP
    }
      "#titleProblem *" #> titleP  &
      "#descriptionProblem *" #> descriptionP
  }

  def getLastCode:CssSel = {
    val t = S.param("t").getOrElse("0")
    val tpt = aTrays.find(_._id.toString == t).getOrElse(TestProblemTry.create)
    "#codeSolveProblemEdit *" #> tpt.code
  }

  def runCode:CssSel = {
    var idP = problem._id.toString
    var codeTest = ""
    var lang = "cpp"
    def runCode(): Unit = {
      val test = TestProblemTry.create
      test.code = codeTest
      test.lang = lang
      test.aDate = (new Date()).getTime
      test.author = user.id.get
      test.problem = problem._id
      test.save
      TestProblemRunner.run(test)
    }
    "#idP" #> SHtml.text(idP, idP = _) &
    "#langCode" #> lang &
    "#sendCode" #> SHtml.textarea(codeTest, codeTest = _) &
    "#runCode" #> SHtml.submit("Run", runCode, "style" -> "display: none")
  }
  def showLastTests:CssSel = {
    "#checkTr *" #> aTrays.map(aTray =>
      "tr [id]" #> aTray._id.toString &
        ".col1 *" #> Formater.formatTimeForSort(new Date(aTray.aDate)) &
      ".col2 *" #> (aTray.good.filter(_ == true).size.toString + "/" + aTray.good.size.toString ) &
      ".col3 *" #> (if(aTray.checked)  <span class="aDot aDotColorGreen"></span>
        else <span class="aDot aDotColorRed"></span>) &
      ".col4 a [href]" #> ("/educontent/checkproblem/" + problem._id.toString + "?t=" + aTray._id.toString)
    ) &
    "#refreshProblemAnchor [href]" #> ("/educontent/checkproblem/" + id)
  }
}
