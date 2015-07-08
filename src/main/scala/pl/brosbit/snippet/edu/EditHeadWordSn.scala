package pl.brosbit.snippet.edu

import java.util.Date
import scala.xml.{Text, XML, Unparsed, Source}
import _root_.net.liftweb._
import http.{S, SHtml}
import common._
import util._
import mapper.{OrderBy, Descending}
import pl.brosbit.model._
import edu._
import mapper.By
import json.JsonDSL._
import json.JsonAST.JObject
import json.JsonParser
import org.bson.types.ObjectId
import Helpers._

class EditHeadWordSn {

  val user = User.currentUser.openOrThrowException("Niezalogowany nauczyciel")
  val id = S.param("id").openOr("0")
  val subId = S.param("s").openOr("0");
  var headWord = if (id == "0") HeadWord.create else HeadWord.find(id).getOrElse(HeadWord.create)
  val subIdLong = if (headWord.subjectId == 0L) subId.toLong else headWord.subjectId
  var subjectNow = SubjectTeach.findAll(("id" -> subIdLong) ~ ("authorId" -> user.id.get)) match {
    case sub :: list => sub
    case _ => S.redirectTo("/educontent/headwords")
  }

  //for showheadWords - viewer
  def headWordData() = {
    "#title" #> <span>
      {headWord.title}
    </span> &
      "#subject" #> <span>
        {headWord.subjectName}
      </span>
    //"#headWordHTML" #>  Unparsed(headWordCont.sections.join(""))
  }


  //edit headWords 
  def formEdit() = {

    var ID = headWord._id.toString
    var title = headWord.title
    var subjectName = ""
    var subjectLev = headWord.lev.toString
    var headWordsString = headWord.content
    var department = headWord.department
    //println("------------headWords data -----------------\n" +headWordsData)

    val departments = subjectNow.departments.map(s => {
      (s, s)
    })
    val levList = List(("1", "podstawowy"), ("2", "średni"), ("3", "rozszerzony"))

    //poprawić - uwzględnić fak, że new HeadWord już istnieje - chyba, że potrzebujemy kopi
    def saveData() {
      val userId = User.currentUser.openOrThrowException("NOT LOGGED USER").id.is
      if (headWord.authorId == 0L || headWord.authorId == userId) {
        val headWordsContentHtml = Unparsed(headWordsString)

        headWord.title = title
        headWord.subjectId = subjectNow.id
        headWord.subjectName = subjectNow.name
        headWord.lev = subjectLev.toInt
        headWord.department = department
        headWord.authorId = userId
        headWord.content = headWordsString
        headWord.save
      }
      S.redirectTo("/educontent/editheadword/" + headWord._id.toString) //!important must refresh page
    }

    def deleteData() {
      val userId = User.currentUser.openOrThrowException("NOT LOGGED USER").id.is
      if (id != "0") HeadWord.find(id) match {
        case Some(headWord) if headWord.authorId == userId => {
          headWord.delete
        }
        case _ =>
      }
      S.redirectTo("/educontent/headwords")
    }

    def cancelAction() {
      S.redirectTo("/educontent/headwords")
    }


    "#id" #> SHtml.text(ID, ID = _, "type" -> "hidden") &
      "#headWord" #> SHtml.text(title, title = _, "class" -> "Name") &
      "#subject" #> SHtml.text(subjectNow.name, subjectName = _, "readonly" -> "readonly") &
      "#subjectLevel" #> SHtml.select(levList, Full(subjectLev), subjectLev = _) &
      "#department" #> SHtml.select(departments, Full(department), department = _) &
      "#headWordsData" #> SHtml.text(headWordsString, headWordsString = _, "type" -> "hidden") &
      "#save" #> SHtml.button(<span class="glyphicon glyphicon-floppy-save"></span> ++ Text(" Zapisz"), saveData, "title" -> "Zapisz") &
      "#delete" #> SHtml.button(<span class="glyphicon glyphicon-trash"></span> ++ Text(" Usuń "), deleteData, "title" -> "Usuń") &
      "#cancel" #> SHtml.button(<span class="glyphicon glyphicon-share-alt"></span> ++ Text(" Anuluj "), cancelAction, "title" -> "Anuluj")
  }

}
