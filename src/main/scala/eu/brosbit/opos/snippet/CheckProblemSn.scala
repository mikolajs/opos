package eu.brosbit.opos.snippet

import eu.brosbit.opos.lib.Formater
import eu.brosbit.opos.model.{TestProblem, TestProblemTry, User}
import net.liftweb.http.{S, SHtml}
import net.liftweb.json.JsonDSL._
import net.liftweb.util.CssSel
import net.liftweb.util.Helpers._

import java.util.Date
import scala.xml.Unparsed

class CheckProblemSn {
  val baseDir = "/view"
  var id = S.param("id").getOrElse("0")
  var user = User.currentUser.getOrElse(S.redirectTo("/login"))
  println("im a user " + user.id.get)
  val problem = if(id == "0") TestProblem.create else TestProblem.find(id).getOrElse(TestProblem.create)
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
      "#descriptionProblem *" #> Unparsed(descriptionP)
  }
//not used!!
  def getLastCode:CssSel = {
    val t = S.param("t").getOrElse("0")
    val tpt = aTrays.find(_._id.toString == t).getOrElse(TestProblemTry.create)
    "#codeSolveProblemEdit *" #> tpt.code &
    "#lastLang [value]" #> tpt.lang
  }

  def runCode:CssSel = {
    var idP = problem._id.toString
    var codeTest = ""
    var lang = ""
    def runCode(): Unit = {
      val test = TestProblemTry.create
      test.code = codeTest
      test.lang = lang
      test.aDate = new Date().getTime
      test.author = user.id.get
      test.authorInfo = user.getFullName + " " + user.classInfo
      test.problem = problem._id
      test.save
    }
    "#idP" #> SHtml.text(idP, idP = _) &
      "#langCode" #> SHtml.text(lang, lang = _) &
      "#sendCode" #> SHtml.textarea(codeTest, codeTest = _) &
      "#runCode" #> SHtml.submit("Run", runCode, "style" -> "display: none")
  }
  def showLastTests:CssSel = {
    "#checkTr *" #> aTrays.map(aTray =>
      "tr [id]" #> aTray._id.toString &
        ".col1 *" #> Formater.formatTimeForSort(new Date(aTray.aDate)) &
        ".col2 *" #>  (if(!aTray.checked) <p>W trakcie</p> else mkResultInfo(aTray)) &
        ".col3 *" #> (if(aTray.checked)  <span class="aDot aDotColorGreen"></span>
        else <span class="aDot aDotColorRed"></span>) &
        ".col4 *" #> aTray.lang &
        ".col5 button [onclick]" #> (s"""checkProblem.insertCode(this);""") &
        ".col5 .code *" #> (aTray.code)
    ) &
      "#refreshProblemAnchor [href]" #> (baseDir + "/checkproblem/" + id)
  }
  private def mkResultInfo(aTray:TestProblemTry) ={
    if(aTray.good)  <p><span style="color:green;font-size:1.5em;">&#x2714;</span> {aTray.outputs}</p>
    else <p><span style="color:red;font-size:1.5em;">&#x2718;</span> {aTray.outputs}</p>
  }
}
