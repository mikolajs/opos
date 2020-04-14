package eu.brosbit.opos.snippet.edu

import eu.brosbit.opos._
import model._
import edu._
import scala.xml._
import _root_.net.liftweb._
import common._
import util._
import mapper.{OrderBy, Ascending}
import http.{S, SHtml}
import json.JsonDSL._
import json.JsonAST.JObject
import json.JsonParser
import http.js.JsCmds.{SetHtml, Alert}
import http.js.JsCmd
import org.bson.types.ObjectId
import Helpers._
import eu.brosbit.opos.snippet._

class OptionsSn {

  val user = User.currentUser.openOrThrowException("Niezalogowany nauczyciel")
  val subjectTeach = SubjectTeach.findAll(("authorId" -> user.id.get), ("prior" -> 1))
  val levStr = S.param("l").openOr("1")
  val levList = List(("1", "podstawowy"), ("2", "średni"), ("3", "rozszerzony"))
  val levMap = levList.toMap

  def showAllSubjects() = {
    val subjectsAll = SubjectName.findAll(OrderBy(SubjectName.nr, Ascending))
      .filter(s => !subjectTeach.exists(t => t.id == s.id.get))
    "li *" #> subjectsAll.map(s =>
      (<button class="btn btn-info" onclick="subjects.addSubject(this)">
        <span class="glyphicon glyphicon-plus-sign"></span>
      </button>
        ++ <span class="subjectName" id={"id_" + s.id.toString}>
        {s.name}
      </span>))
  }

  def showMySubjects() = {

    "li" #> subjectTeach.map(s => (<li class="list-group-item">
      <button class="btn btn-danger" onclick="subjects.removeSubject(this)">
        <span class="glyphicon glyphicon-remove-sign"></span>
      </button>
      <span class="subjectName" id={"id_" + s.id.toString}>
        {s.name}
      </span>
    </li>))
  }

  def formEdit() = {
    var data = ""
    def save() {
      var notdeleted = true
      var subjectsT = SubjectTeach.findAll(("authorId" -> user.id.get))
      data.split('|').map(d => {
        val sub = d.split(";")
        if (sub.length == 3) {
          val id = tryo(sub.head.toLong).openOr(0L)
          val nr = tryo(sub(1).toInt).openOr(0)
          if (id != 0L && nr != 0) {
            val (subHead, rest) = subjectsT.partition(s => s.id == id)
            val subT = if (subHead.isEmpty) SubjectTeach.create
            else {
              subHead.tail.foreach(s => s.delete)
              subjectsT = rest
              subHead.head
            }
            subT.authorId = user.id.get
            subT.name = sub.last.trim
            subT.id = id
            subT.prior = nr
            subT.save
          }
        }
      })
      subjectsT.foreach(s => s.delete)
      initializeSubjectAndLevelChoice
    }
    "#dataContent" #> SHtml.text("", data = _, "display" -> "none") &
      "#save" #> SHtml.button(<span class="glyphicon glyphicon-floppy-save">Zapisz zmiany!</span>, save, "class" -> "btn btn-success btn-lg")
  }

  def deleteDepartment() = {
    var department = ""
    var subjectId = ""
    def delete() {
      subjectTeach.find(st => st._id.toString() == subjectId) match {
        case Some(subjectT) => {
          subjectT.departments = subjectT.departments.filterNot(d => d == department)
          subjectT.save
        }
        case _ =>
      }
    }

    "#departmentName" #> SHtml.text(department, department = _) &
      "#subject" #> SHtml.text(subjectId, subjectId = _)
  }

  def showDepartments() = {
    var depart = ""
    var subjID = 0L
    if (subjectTeach.length > 0) {

      var subjectNow = subjectTeach.find(s => subjID == s.id).getOrElse(subjectTeach.head)
      def deleteDep(dep: String): JsCmd = {
        subjectNow.departments = subjectNow.departments.filter(d => d != dep)
        subjectNow.save
        val departs = subjectNow.departments.map(d =>
          <li class="list-group-item">
            {d}<span>
            {SHtml.a(() => deleteDep(d), <span class="glyphicon glyphicon-remove-sign"></span>)}
          </span>
          </li>)
        SetHtml("departmentsList", departs)
      }

      def redirect(ID: String) = {
        val idL = tryo(ID.toLong).openOr(0L)
        subjectNow = subjectTeach.find(s => idL == s.id).getOrElse(subjectTeach.head)
        val departs = subjectNow.departments.map(d =>
          <li class="list-group-item">
            {d}<span>
            {SHtml.a(() => deleteDep(d), <span class="glyphicon glyphicon-remove-sign"></span>)}
          </span>
          </li>)
        SetHtml("departmentsList", departs)
      }
      def saveDep(): JsCmd = {
        subjectNow.departments = depart :: subjectNow.departments
        subjectNow.save
        depart = ""
        val departs = subjectNow.departments.map(d =>
          <li class="list-group-item">
            {d}<span>
            {SHtml.a(() => deleteDep(d), <span class="glyphicon glyphicon-remove-sign"></span>)}
          </span>
          </li>)
        SetHtml("departmentsList", departs)
      }
      val subjectsList = subjectTeach.map(s => (s.id.toString, s.name))

      val depLI = subjectNow.departments.map(d =>
        <li class="list-group-item">
          {d}<span>
          {SHtml.a(() => deleteDep(d), <span class="glyphicon glyphicon-remove-sign"></span>)}
        </span>
        </li>)
      println("SubjectNOW=== " + subjectNow.name + " id: " + subjectNow.id.toString + "\n LIST: \n" + depLI.mkString(" "))
      val form =
        "#selectEdit" #> SHtml.ajaxSelect(subjectsList, Full(subjectNow.id.toString), ID => redirect(ID)) &
          "#addInput" #> SHtml.text(depart, depart = _) &
          "li" #> depLI &
          "#saveDep" #> SHtml.ajaxSubmit("Zapisz", saveDep) andThen SHtml.makeFormsAjax

      "form" #> (in => form(in))
    }
    else "form" #> <span></span>
  }

  def showSubjetsLev() = {
    "li" #> subjectTeach.map(s => <li class="list-group-item">
      {s.name + " - " + levMap(s.lev.toString)}
    </li>)
  }

  def editLevels() = {
    var subject = ""
    var level = ""
    def save() {
      val levInt = tryo(level.toInt).openOr(0)
      val subjectId = tryo(subject.toLong).openOr(0L)
      subjectTeach.find(s => s.id == subjectId) match {
        case Some(sub) => {
          sub.lev = levInt
          sub.save
        }
        case _ =>
      }

    }
    val subj = subjectTeach.map(s => (s.id.toString, s.name))

    "#subjectsLevel" #> SHtml.select(subj, Full(subject), subject = _) &
      "#levelEdit" #> SHtml.select(levList, Full("1"), level = _) &
      "#saveLevel" #> SHtml.submit("Zapisz", save)

  }

  private def initializeSubjectAndLevelChoice() {
    if (subjectTeach.length > 0) {
      SubjectChoose.set(subjectTeach.head.id)
      LevelChoose.set(subjectTeach.head.lev)
    }

  }
}
