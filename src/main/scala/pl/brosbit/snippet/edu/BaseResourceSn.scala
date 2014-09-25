package pl.brosbit.snippet.edu

import pl.brosbit._
import model._
import edu._
import scala.xml.{NodeSeq, Unparsed}
import _root_.net.liftweb._
import common._
import util._
import http.{ S, SHtml }
import json.JsonDSL._
import json.JsonAST.JObject
import json.JsonParser
import org.bson.types.ObjectId
import Helpers._

trait BaseResourceSn {

  val user = User.currentUser.openOrThrowException("Niezalogowany nauczyciel")
  val subjectTeach = SubjectTeach.findAll(("authorId" -> user.id.is),("$orderby"->("prior"->1)))
  if (subjectTeach.isEmpty && S.uri.split("/").last != "options") S.redirectTo("/educontent/options")
  val subjId = S.param("s").openOr(subjectTeach.head.id.toString)
  val subjectNow = subjectTeach.find(s => s.id.toString() == subjId).getOrElse(subjectTeach.head)
  val levStr = S.param("l").openOr(subjectNow.lev.toString)
  val levList = List(("1", "podstawowy"), ("2", "rozszerzony"))
  val levMap = levList.toMap


  def techerSubjects() = {
    val subj = subjectTeach.map(s => (s.id, s.name))
    "#subjectSelect" #> subj.map(s =>
      "option" #> <option value={ s._1.toString }>{ s._2 }</option>)
  }

  def findSubjectName(id: Long): String = {
    for (s <- subjectTeach) {
      if (s.id == id) return s.name
    }
    ""
  }
  
  def findSubjectId(name:String) = {
    val matched = subjectTeach.filter(s => s.name == name)
    if(matched.isEmpty) subjectNow.id else matched.head.id
  }
  
  
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