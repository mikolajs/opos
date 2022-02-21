package eu.brosbit.opos.snippet.edu

import eu.brosbit.opos.lib.{Formater, TestProblemRunner}
import eu.brosbit.opos.model.{Problems, TestProblemTry, User}
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
  val problem = if(id == "0") Problems.create else Problems.find(id).getOrElse(Problems.create)
  if(problem.author != 0 && problem.author != user.id.get ) S.redirectTo("/educontent/problems")
  else if(problem.author == 0L) problem.author = user.id.get
  def show:CssSel = {
    var titleP = problem.title
    var infoP = problem.info
    var descriptionP = problem.description
    def saveProblem() {
      problem.description = descriptionP
      problem.title = titleP
    }
      "#titleProblem *" #> titleP  &
      "#descriptionProblem *" #> descriptionP
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
    "#codeSolveProblemHidden" #> SHtml.textarea(codeTest, codeTest = _) &
    "#runCode" #> SHtml.submit("Run", runCode, "style" -> "display: none")
  }
  def showLastTests:CssSel = {
    val aTrays = TestProblemTry.findAll(("problem" -> problem._id.toString)~("author" -> user.id.get) )
    "#check" #> aTrays.map(aTray =>
      "tr [id]" #> aTray._id.toString &
        ".col1 *" #> Formater.formatTimeForSort(new Date(aTray.aDate)) &
      ".col2 *" #> (aTray.good.filter(_ == true).size.toString + "/" + aTray.good.size.toString ) &
      ".col3 *" #> (if(aTray.checked)  <span class="aDot aDotColorGreen"></span>
        else <span class="aDot aDotColorRed"></span>) &
      ".col4 *" #> aTray.code
    ) &
    "#refreshProblemAnchor [href]" #> ("educontent/problem/" + id)
  }
}
