package pl.brosbit.snippet.edu

import scala.xml.{Text,XML,Unparsed}
import _root_.net.liftweb._
import http.{S,SHtml}
import common._
import util._
import mapper.{OrderBy,Descending}
import pl.brosbit.model._
import edu._
import Helpers._
import json.JsonDSL._
import json.JsonAST.JObject
import json.JsonParser
import org.bson.types.ObjectId
import  _root_.net.liftweb.http.js.JsCmds._
import  _root_.net.liftweb.http.js.JsCmd
import  _root_.net.liftweb.http.js.JE._

class EditQuestSn extends BaseResourceSn {
    
    var subjectPar = S.param("sub").openOr("0")
    var level = S.param("lev").openOr("3")
   val subjectId = tryo(subjectPar.toLong).openOr(subjectTeach.head.id)
        
    
    def choiseQuest() = {
         val subjects = subjectTeach.map(s => (s.id.toString, s.name))
         def makeChoise() {
             S.redirectTo("/resources/editquest?sub="+subjectId+"&lev="+level)
         }
         
        "#subjects" #> SHtml.select(subjects, Full(subjectId.toString), subjectPar = _) &
        "#levels" #> SHtml.select(levList, Full(level), level = _) &
        "#choise" #> SHtml.submit("Wybierz", makeChoise)
    }
    
    def showQuests() = {
        val userId = user.id.is
        
        "tr" #> QuizQuestion.findAll(("authorId"->userId)~("subjectId"->subjectId)~
               ("level"->level.toInt)).map(quest => {
            <tr id={quest._id.toString}><td>{Unparsed(quest.question)}</td>
            <td>{quest.answer}</td><td>{quest.department}</td><td>
            {quest.fake.map(f => <span class="wrong">{f}</span>)}</td>
            <td>{quest.dificult}</td></tr>
        })
    }
    
    //working ....
    def editQuest() = {
        printParam
        var id = ""
        var question = ""
        var public = false
        var answer = ""
        var wrongAnswers =""
        var subject = ""
        var dificult = "2"
         var department = ""
                
        def save():JsCmd = {
            
            val userId = user.id.is
            val quest = QuizQuestion.find(id).getOrElse(QuizQuestion.create)
            if(quest.authorId != 0L && quest.authorId != userId) return Alert("To nie twoje pytanie!")
            quest.authorId = userId
            quest.answer = answer
            quest.fake = wrongAnswers.split(";").toList
            quest.question = question
            quest.subjectId = tryo(subject.toLong).openOr(subjectTeach.head.id)
            quest.department = department
            quest.dificult = tryo(dificult.toInt).openOr(9)
            quest.level = level.toInt
            quest.save
            JsFunc("editQuest.insertQuestion",quest._id.toString).cmd
        }
        
        def delete():JsCmd = {
             println("+++++++++++++++++++ Del QUEST ")
            val userId = user.id.is 
            QuizQuestion.find(id) match {
                case Some(quest) => {
                    if(quest.authorId == userId) {
                        quest.delete
                        JsFunc("editQuest.deleteQuestion", quest._id.toString).cmd
                    }
                    else Alert("To nie twoje pytanie!")
                }
                case _ => Alert("Nie znaleziono pytania!")
            }
        }

        val dificults = 2 to 6 map( i => {val iS = i.toString;(iS, iS)})        
        
       val form = "#idQuest" #> SHtml.text(id, id = _) &
       "#questionQuest" #> SHtml.textarea(question, x => question = x.trim) &
       "#answerQuest" #> SHtml.text(answer, x => answer = x.trim) &
       "#wrongQuest" #> SHtml.text(wrongAnswers, x => wrongAnswers = x.trim) &
       "#dificultQuest" #> SHtml.select(dificults, Full(dificult), dificult = _) &
       "#publicQuest" #> SHtml.checkbox(public, public = _, "id"->"publicQuest") &
       "#saveQuest" #> SHtml.ajaxSubmit("Zapisz",save) &
       "#deleteQuest" #> SHtml.ajaxSubmit("Usuń",delete) andThen SHtml.makeFormsAjax
       
        "form" #> (in => form(in))
       
    }
 
    
    private def printParam = println("subjectId="+ subjectId + " level=" + level)

}