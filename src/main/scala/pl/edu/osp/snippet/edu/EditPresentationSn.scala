package pl.edu.osp.snippet.edu


import scala.xml.{Text, Unparsed}
import _root_.net.liftweb._
import http.{S, SHtml}
import common._
import util._
import pl.edu.osp.model._
import pl.edu.osp.model.edu.{Slide, SlideContent, SubjectTeach}
import json.JsonDSL._
import Helpers._

class EditPresentationSn {

  val user = User.currentUser.openOrThrowException("Niezalogowany nauczyciel")
  val id = S.param("id").openOr("0")
  val subId = S.param("s").openOr("0")
  var slideHead = if (id == "0") Slide.create else Slide.find(id).getOrElse(Slide.create)
  var slideCont = SlideContent.find(slideHead.slides.toString).getOrElse(SlideContent.create)
  val subIdLong = if (slideHead.subjectId == 0L) subId.toLong else slideHead.subjectId
  var subjectNow = SubjectTeach.findAll(("id" -> subIdLong) ~ ("authorId" -> user.id.get)) match {
    case sub :: list => sub
    case _ => S.redirectTo("/educontent/presentations")
  }
  var departNr= tryo(S.param("d").openOr("0").toInt).getOrElse(0);
  var departName = departNr match {
    case -1 => ""
    case 0 => if(subjectNow.departments.isEmpty) "" else subjectNow.departments.head
    case nr:Int if(subjectNow.departments.length > nr ) =>   subjectNow.departments(nr)
    case _ => if(subjectNow.departments.isEmpty) "" else subjectNow.departments.head
  }


  //for showSlides - viewer
  def slidesData() = {
    "#title" #> <span>
      {slideHead.title}
    </span> &
      "#subject" #> <span>
        {slideHead.subjectName}
      </span>
    //"#headWordHTML" #>  Unparsed(headWordCont.sections.join(""))
  }


  //edit headWords
  def formEdit() = {

    var ID = slideHead._id.toString
    var title = slideHead.title
    var subjectName = ""
    var subjectLev = slideHead.lev.toString
    var contentString = slideCont.slides
    var department = if(slideHead.department.isEmpty)  departName
                      else slideHead.department
    var description = slideHead.descript
    //println("------------headWords data -----------------\n" +headWordsData)

    val departments = subjectNow.departments.map(s => {
      (s, s)
    })
    val levList = List(("1", "podstawowy"), ("2", "średni"), ("3", "rozszerzony"))

    def saveData() {
      val userId = User.currentUser.openOrThrowException("NOT LOGGED USER").id.get
      if (slideHead.authorId == 0L || slideHead.authorId == userId) {
        //val contentHtml = Unparsed(contentString)
        slideHead.title = title
        slideHead.subjectId = subjectNow.id
        slideHead.subjectName = subjectNow.name
        slideHead.lev = subjectLev.toInt
        slideHead.department = department
        slideHead.authorId = userId
        slideHead.descript = description
        slideCont.slides = contentString
        slideHead.slides = slideCont._id
        slideHead.save
        slideCont.save
      }
      S.redirectTo("/educontent/editpresentation/" + slideHead._id.toString) //!important must refresh page
    }

    def deleteData() {
      val userId = User.currentUser.openOrThrowException("NOT LOGGED USER").id.get
      if (id != "0" && slideHead.authorId == userId){
          slideHead.delete
          slideCont.delete
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

  def show() = {
    "a [href]" #> ("/showslide/" + id)
  }
}
