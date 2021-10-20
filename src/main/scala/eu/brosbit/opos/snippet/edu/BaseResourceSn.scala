package eu.brosbit.opos.snippet.edu

import eu.brosbit.opos._
import model._
import edu._
import scala.xml.{NodeSeq, Unparsed}
import _root_.net.liftweb._
import common._
import util._
import http.{S, SHtml}
import json.JsonDSL._
import json.JsonAST.JObject
import json.JsonParser
import _root_.net.liftweb.http.js.JsCmds._
import _root_.net.liftweb.http.js.JsCmd
import org.bson.types.ObjectId
import Helpers._

trait BaseResourceSn {

  protected val user: User = User.currentUser.openOrThrowException("Niezalogowany nauczyciel")
  protected val subjectTeach = SubjectTeach.findAll(("authorId" -> user.id.get), ("prior" -> 1))
  if (subjectTeach.isEmpty && S.uri.split("/").last != "options")
    S.redirectTo("/educontent/options")
  protected val subjId = S.param("s").openOr(subjectTeach.head.id.toString)
  protected val subjectNow = subjectTeach.find(s =>
    s.id.toString() == subjId).getOrElse(subjectTeach.head)
  //val levStr = S.param("l").openOr(subjectNow.lev.toString)
  protected val subjectId = subjectNow.id
  val levList = List(("1", "podstawowy"), ("2", "rozszerzony"), ("3", "konkursowy"))
  protected val levMap = levList.toMap

  protected val departNr = tryo(S.param("d").openOr("0").toInt).openOr(0)
  protected val departName = departNr match {
    case -1 => ""
    case 0 => if(subjectNow.departments.isEmpty) "" else subjectNow.departments.head
    case nr:Int if(subjectNow.departments.length > nr ) =>   subjectNow.departments(nr)
    case _ => if(subjectNow.departments.isEmpty) "" else subjectNow.departments.head
  }

  val query =
    if(departNr < 0)
      ("authorId" -> user.id.get) ~ ("subjectId" -> subjectNow.id)
    else
      ("authorId" -> user.id.get) ~ ("subjectId" -> subjectNow.id) ~("department" -> departName)

  def techerSubjects() = {
    val subj = subjectTeach.map(s => (s.id, s.name))
    "#subjectSelect" #> subj.map(s =>
      "option" #> <option value={s._1.toString}>
        {s._2}
      </option>)
  }

  def findSubjectName(id: Long): String = {
    for (s <- subjectTeach) {
      if (s.id == id) return s.name
    }
    ""
  }

  def findSubjectId(name: String) = {
    val matched = subjectTeach.filter(s => s.name == name)
    if (matched.isEmpty) subjectNow.id else matched.head.id
  }

  def subjectChoice(basePath: String) = {
    def redirect(str: String): JsCmd = {
      S.redirectTo(basePath + "?s=" + str)
    }
    val subjects = subjectTeach.map(s => (s.id.toString(), s.name))
    "#subjectChoice" #> SHtml.ajaxSelect(subjects, Full(subjectNow.id.toString), (str) => redirect(str))
  }

  def subjectAndDepartmentChoice(basePath: String) = {
    def redirectPath(sub: String, depNr: String): String = {
      basePath + "?s=" + sub + "&d=" + depNr
    }
    val subjects = subjectTeach.map(s => {
      <optgroup label={s.name}>
        { var n = -1; s.departments.map(d => {
          n += 1
          <option value={redirectPath(s.id.toString, n.toString)}  >
            {d}</option>
        })} ++ <option value={redirectPath(s.id.toString, (-1).toString)} >Wszystkie</option>
      </optgroup>
    })


    "#subjectChoice" #> <select>{subjects}</select> &
    "h2" #> <h2> <span class="label label-info"> {subjectNow.name}</span> Dział:
      <big id="subjectNameLabel">{if(departName.isEmpty) "Wszystkie" else Unparsed(departName)}</big>
      <small style="display:none;">{redirectPath(subjectNow.id.toString, departNr.toString)}</small>
    </h2>
  }

  def getSeparator = ";#;;#;"

  /*
   def choiceSubjectAndLevel(basePath:String) = {
     val subjects = subjectTeach.map(s => (s.id.toString, s.name))
     var subjectChoice = ""
     var levelChoice = ""
          def makeChoise() {
              S.redirectTo(basePath + "?s=" + subjectChoice+ "&l=" + levelChoice)
          }
         "#subjects" #> SHtml.select(subjects, Full(subjectNow.id.toString), subjectChoice = _) &
         "#levels" #> SHtml.select(levList, Full(levStr), levelChoice = _) &
         "#choise" #> SHtml.submit("Wybierz", makeChoise)
   }
   *
   */
  /*
    def autocompliteScript(in: NodeSeq) = {
      var dataStart = "$(function() {\n" + "var availableTags = ["
      var dataCenter = subjectNow.departments.map(dep => "\"" + dep + "\"").mkString(", ")
      var dataEnd = "];\n $( \"#department\" ).autocomplete({source: availableTags});});"
      <script>{ Unparsed(dataStart + dataCenter + dataEnd) }</script>
    }

    protected def saveNewDepartment(depName: String) = {
      if(depName.length() > 0) {
        if(!subjectNow.departments.exists(d => d == depName)) {
          subjectNow.departments = depName::subjectNow.departments
          subjectNow.save
        }
      }
    }
    */
}