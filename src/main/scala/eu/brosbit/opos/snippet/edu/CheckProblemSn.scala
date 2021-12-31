package eu.brosbit.opos.snippet.edu

import eu.brosbit.opos.model.{Problems, User}
import net.liftweb.util.CssSel
import net.liftweb.util.Helpers._
import net.liftweb.http.{S, SHtml}

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

    }
    "#idP" #> SHtml.text(idP, idP = _) &
    "#codeSolveProblemHidden" #> SHtml.textarea(codeTest, codeTest = _) &
    "#runCode" #> SHtml.submit("Run", runCode, "style" -> "display: none")
  }
  def showLastTests:CssSel = {

    "#check" #> ""
  }
}
