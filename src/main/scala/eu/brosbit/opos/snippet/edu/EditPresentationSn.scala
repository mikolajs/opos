package eu.brosbit.opos.snippet.edu


import scala.xml.Text
import _root_.net.liftweb._
import http.{S, SHtml}
import common._
import util._
import eu.brosbit.opos.model._
import eu.brosbit.opos.model.edu.{Presentation, SubjectTeach}
import json.JsonDSL._
import Helpers._

class EditPresentationSn {

  private val user = User.currentUser.openOrThrowException("Niezalogowany nauczyciel")
  private val id = S.param("id").openOr("0")
  private val subId = S.param("s").openOr("0")
  private val slide = if (id == "0") Presentation.create else Presentation.find(id).getOrElse(Presentation.create)
  private val subIdLong = if (slide.subjectId == 0L) subId.toLong else slide.subjectId
  private val subjectNow = SubjectTeach.findAll(("id" -> subIdLong) ~ ("authorId" -> user.id.get)) match {
    case sub :: _ => sub
    case _ => S.redirectTo("/educontent/presentations")
  }
  private val departNr = tryo(S.param("d").openOr("0").toInt).getOrElse(0)
  private val departName = departNr match {
    case -1 => ""
    case 0 => if(subjectNow.departments.isEmpty) "" else subjectNow.departments.head
    case nr:Int if subjectNow.departments.length > nr  =>   subjectNow.departments(nr)
    case _ => if(subjectNow.departments.isEmpty) "" else subjectNow.departments.head
  }


  //for showSlides - viewer
  def slidesData(): CssSel = {
    "#title" #> <span>
      {slide.title}
    </span> &
      "#subject" #> <span>
        {slide.subjectName}
      </span>
    //"#headWordHTML" #>  Unparsed(headWordCont.sections.join(""))
  }

  //edit headWords
  def formEdit(): CssSel = {

    var ID = slide._id.toString
    var title = slide.title
    var subjectName = ""
    var subjectLev = slide.lev.toString
    var contentString = slide.slides
    var department = if(slide.department.isEmpty)  departName
                      else slide.department
    var description = slide.descript
    //println("------------headWords data -----------------\n" +headWordsData)

    val departments = subjectNow.departments.map(s => {
      (s, s)
    })
    val levList = List(("1", "podstawowy"), ("2", "średni"), ("3", "rozszerzony"))

    def saveData() {
      val userId = User.currentUser.openOrThrowException("NOT LOGGED USER").id.get
      if (slide.authorId == 0L || slide.authorId == userId) {
        //val contentHtml = Unparsed(contentString)
        slide.title = title
        slide.subjectId = subjectNow.id
        slide.subjectName = subjectNow.name
        slide.lev = subjectLev.toInt
        slide.department = department
        slide.authorId = userId
        slide.descript = description
        slide.slides = contentString
        slide.save
      }
      S.redirectTo("/educontent/editpresentation/" + slide._id.toString) //!important must refresh page
    }

    def deleteData() {
      val userId = User.currentUser.openOrThrowException("NOT LOGGED USER").id.get
      if (id != "0" && slide.authorId == userId){
          slide.delete
      }
      S.redirectTo("/educontent/presentations")
    }

    def cancelAction() {
      S.redirectTo("/educontent/presentations")
    }

    "#id" #> SHtml.text(ID, ID = _, "type" -> "hidden") &
      "#title" #> SHtml.text(title, title = _, "class" -> "Name") &
      "#subject" #> SHtml.text(subjectNow.name, subjectName = _, "readonly" -> "readonly") &
      "#subjectLevel" #> SHtml.select(levList, Full(subjectLev), subjectLev = _) &
      "#department" #> SHtml.select(departments, Full(department), department = _) &
      "#description" #> SHtml.textarea(description, description = _) &
      "#slidesData" #> SHtml.textarea(contentString, contentString = _) &
      "#save" #> SHtml.button(<span class="glyphicon glyphicon-floppy-save"></span> ++ Text(" Zapisz"), saveData, "title" -> "Zapisz") &
      "#delete" #> SHtml.button(<span class="glyphicon glyphicon-trash"></span> ++ Text(" Usuń "), deleteData, "title" -> "Usuń") &
      "#cancel" #> SHtml.button(<span class="glyphicon glyphicon-share-alt"></span> ++ Text(" Anuluj "), cancelAction, "title" -> "Anuluj")
  }

  def show(): CssSel = {
    "a [href]" #> ("/showslide/" + id)
  }
}
