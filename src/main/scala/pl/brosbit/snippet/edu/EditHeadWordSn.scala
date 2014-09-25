package pl.brosbit.snippet.edu

import java.util.Date
import scala.xml.{Text,XML,Unparsed, Source}
import _root_.net.liftweb._
import http.{S,SHtml}
import common._
import util._
import mapper.{OrderBy,Descending}
import pl.brosbit.model._
import edu._
import mapper.By
import json.JsonDSL._
import json.JsonAST.JObject
import json.JsonParser
import org.bson.types.ObjectId
import Helpers._

class  EditHeadWordSn  extends BaseResourceSn with RoleChecker {
  
 val id = S.param("id").openOr("0")
 var headWord = if (id != "0") HeadWord.find(id).getOrElse(HeadWord.create) else HeadWord.create
 
 //for showheadWords - viewer 
  def headWordData() = {
      "#title" #> <span>{headWord.title}</span> &
      "#subject" #> <span>{headWord.subjectName}</span> 
      //"#headWordHTML" #>  Unparsed(headWordCont.sections.join("")) 
  }
  
  
  //edit headWords 
  def formEdit() = {
    
    var ID = if(id == "0") "0" else headWord._id.toString
    var title = headWord.title
    var subjectId = if(headWord.subjectId != 0L) headWord.subjectId.toString else ""
     var subjectLev = headWord.lev.toString
    var headWordsString = headWord.content
    //println("------------headWords data -----------------\n" +headWordsData)
     
    val listSubject = subjectTeach.map(s => {(s.id.toString ,s.name)})
    
    //poprawić - uwzględnić fak, że new HeadWord już istnieje - chyba, że potrzebujemy kopi
    def saveData() {
      val userId = User.currentUser.openOrThrowException("NOT LOGGED USER").id.is
      if(headWord.authorId == 0L || headWord.authorId == userId) {
          val headWordsContentHtml = Unparsed(headWordsString)
          
          headWord.title = title
          headWord.subjectId = tryo(subjectId.toLong).openOr(0L)
          headWord.subjectName = findSubjectName(headWord.subjectId)
          headWord.lev = subjectLev.toInt
          headWord.authorId = userId
          headWord.content = headWordsString      
          headWord.save 
      }
      S.redirectTo("/educontent/editheadword/"+ headWord._id.toString) //!important must refresh page
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

    val publicList = List(("TAK","TAK"),("NIE","NIE"))
    "#id" #> SHtml.text(ID, ID = _, "type"->"hidden") &
    "#headWord" #> SHtml.text(title, title= _,"class"->"Name") &
    "#subjects" #> SHtml.select(listSubject, Full(subjectId),subjectId = _) &
    "#subjectLevel" #> SHtml.select(levList,Full(subjectLev),subjectLev = _) &
    "#headWordsData" #> SHtml.text(headWordsString, headWordsString = _, "type"->"hidden") &
    "#save" #> SHtml.button(<span class="glyphicon glyphicon-floppy-save"></span>  ++ Text(" Zapisz"), saveData,"title"->"Zapisz") &
    "#delete" #> SHtml.button(<span class="glyphicon glyphicon-trash"></span>  ++ Text(" Usuń "),  deleteData,"title"->"Usuń")  &
    "#cancel" #> SHtml.button(<span class="glyphicon glyphicon-share-alt"></span>  ++ Text(" Anuluj "), cancelAction,"title"->"Anuluj") 
  }
 
}
