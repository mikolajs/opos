package pl.edu.osp.snippet.edu

import scala.xml.{Text, XML, Unparsed, NodeSeq}
import _root_.net.liftweb._
import _root_.net.liftweb.http.{S, SHtml}
import _root_.net.liftweb.common._
import pl.edu.osp.model.edu._
import pl.edu.osp.lib.DataTableOption._
import pl.edu.osp.lib.{DataTable}
import json.DefaultFormats
import json.JsonDSL._
import json.JsonParser._
import _root_.net.liftweb.http.js.JsCmds._
import _root_.net.liftweb.http.js.JsCmd
import _root_.net.liftweb.util.Helpers._


//case class TestJ(l: String, t: String)

class EditLesson extends BaseLesson {

  val subjectTeach = SubjectTeach.findAll(("authorId" -> user.id.get),  ("prior" -> 1))
  val subjectNow = subjectTeach.find(s => s.id == subjectId).getOrElse(subjectTeach.head)

  val departList = subjectNow.departments.map(d => (d.name -> d.name))


  def showCourseInfo() = {
    if (courseOption.isEmpty) {
      "h2" #> (<h2>Nie znaleziono kursu!</h2> ++
        <p>Utwórz najpierw kurs, a następnie kliknij na edycję i dodaj lekcję.
        Dopiero wtedy możesz ją edytować</p>)
    } else {
      val course = courseOption.get
      "h2" #>
        (<h2>
          {course.title}<span class="mutted">- klasy:
            {course.classInfo}
          </span>
        </h2> ++ <p>
          {course.descript}<br/> <span class="mutted">Przedmiot:</span>{course.subjectName}
        </p>)
    }

  }

  def editLesson() = {

    var id = idPar

    var title = lesson.title
    var nr = lesson.nr
    var descript = lesson.descript
    var chapter = lesson.chapter
    val chapterOld = lesson.chapter
    var newChapter = ""
    var newElem = false
    var json = "[" + lesson.contents.map(cont => cont.forJSONStr).mkString(", ") + "]"

    val userId = user.id.get

    def save() {
      if ((!courseOption.isEmpty) && (lesson.authorId == 0L || lesson.authorId == userId)) {
        lesson.title = title
        lesson.authorId = userId
        lesson.nr = nr
        lesson.descript = descript
        val cour = courseOption.get
        if (newElem) {
          newChapter = newChapter.trim
          if (newChapter.length > 1) {
            chapters.find(ch => ch == newChapter) match {
              case Some(chap) => Unit
              case _ => {
                cour.chapters = cour.chapters ++ List(newChapter)
                cour.save
              }
            }
            lesson.chapter = newChapter
          }
        } else lesson.chapter = chapter
        lesson.contents = createLessonContentsList(json)
        lesson.courseId = cour._id
        lesson.save
      }

      deleteChapterIfLast(chapterOld)
      S.redirectTo("/educontent/course/" + lesson.courseId.toString + "?l=" + lesson._id.toString())
    }

    def delete() {
      if (isLessonOwner(lesson)) lesson.delete
      deleteChapterIfLast(chapterOld)
      println("Usunięcie lekcji: " + lesson.title + " user: " + lesson.authorId)
      S.redirectTo("/educontent/course/" + lesson.courseId.toString)
    }

    val subj = subjectTeach.find(s =>
      courseOption.getOrElse(Course.create).subjectId == s.id).getOrElse(subjectTeach.head)
    //val departs = subj.departments.map(d => (d, d))
    "#ID" #> SHtml.text(id, id = _) &
      "#title" #> SHtml.text(title, x => title = x.trim) &
      "#nr" #> SHtml.number(nr, nr = _, 1, 200) &
      "#chapterNameIsNew" #> SHtml.checkbox_id(newElem, (x: Boolean) => newElem = x, Full("chapterNameIsNew")) &
      "#chapterNameExists" #> SHtml.select(chaptersList, Full(chapter), chapter = _) &
      "#chapterNameNew" #> SHtml.text(newChapter, newChapter = _) &
      "#description" #> SHtml.textarea(descript, descript = _) &
      "#json" #> SHtml.text(json, json = _) &
      "#save" #> SHtml.button(<span class="glyphicon glyphicon-plus-sign"></span> ++ Text("Zapisz"), save) &
      "#delete" #> SHtml.button(<span class="glyphicon glyphicon-remove-sign"></span> ++ Text("Usuń!"),
        delete, "onclick" -> "return confirm('Na pewno usunąć całą lekcję?')")
  }

  def ajaxText = {

    var itemCh = ""
    //var level = ""
    var department = ""

    def getData = {
      //val levInt = tryo(level.toInt).getOrElse(1)
      val lookingQuest = ("authorId" -> user.id.get) ~ ("department" -> department)
      itemCh match {
        case "q" => {
          val str = QuizQuestion.findAll(lookingQuest)
            .map(q => "[ '" + q._id.toString + "',  '" + q.question + "', '" + q.department + "']")
            .mkString(",")
          "[" + str + "]"
        }
        case "w" => {
          val str = HeadWord.findAll(lookingQuest)
            .map(h => "[ '" + h._id.toString + "',  '" + h.title + "', '" + "']")
            .mkString(",")
          "[" + str + "]"
        }
        case "v" => {
          val str = Video.findAll(lookingQuest)
            .map(h => "[ '" + h._id.toString + "',  '" + h.title + "', '" + h.descript + "']")
            .mkString(",")
          "[" + str + "]"
        }
        case "d" => {
          val str = Document.findAll(lookingQuest)
            .map(d => "['" + d._id.toString + "', '" + d.title + "', '" + d.descript + "']")
            .mkString(",")
          "[" + str + "]"
        }
        case _ => "error"
      }
    }

    def refreshData(): JsCmd = {
      println("[AppINFO]:: Ajax Hidden text refresh; itemType= " +
        itemCh  + " depart= " + department)
      SetValById("jsonForDataTable", getData) & Run("refreshTab();")
    }

    val itemTypes = List(("w" -> "Hasła"), ("d" -> "Artykuły"),
      ("q" -> "Zadania"), ("v" -> "Filmy"), ("n" -> "Notatka"))

    val form = "#getItemType" #> SHtml.select(itemTypes, Full(itemCh), itemCh = _) &
      //"#getLevel" #> SHtml.select(levList, Full(level), level = _) &
      "#getDepartment" #> SHtml.select(departList, Full(department), department = _) &
      "#getItems" #> SHtml.ajaxSubmit("Pokaż", () => refreshData,
        "class" -> "btn btn-lg btn-success", "type" -> "submit") andThen SHtml.makeFormsAjax

    "form" #> (in => form(in))
  }

  def renderLinkAndScript(html: NodeSeq) = DataTable.mergeSources(html)

  def dataTableScript(xhtml: NodeSeq): NodeSeq = {
    val col = List("Id", "Tytul", "Dzial")
    DataTable("#choiceTable",
      LanguageOption("pl"),
      ExtraOptions(Map("sPaginationType" -> "two_button")),
      DataOption(col, Nil),
      SortingOption(Map(1 -> Sorting.ASC)),
      DisplayLengthOption(4, List(4, 10, 20)),
      ColumnNotSearchAndHidden(List(0), List(0)))
  }


  // override def autocompliteScript(in:NodeSeq) = super.autocompliteScript(in)

  private def isLessonOwner(lesson: LessonCourse) = user.id.get == lesson.authorId

  private def createLessonContentsList(jsonStr: String) = {
    implicit val formats = DefaultFormats
    val json = parse(jsonStr)
    json.extract[List[LessonContent]]
  }

  private def deleteChapterIfLast(chapter: String) = {
    if (!courseOption.isEmpty) {
      val course = courseOption.get
      val lessons =
        LessonCourse.findAll(("courseId" -> course._id.toString()) ~ ("chapter" -> chapter))
      if (lessons.isEmpty)
        course.chapters = course.chapters.filterNot(ch => ch == chapter)
      println("[AppINFO:::::: on delete lesson delete chapters if last == " + lessons.length.toString)
      course.save
    }
  }


}

