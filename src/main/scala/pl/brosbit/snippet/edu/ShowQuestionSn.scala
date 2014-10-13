package pl.brosbit.snippet.edu

import java.util.Date
import scala.xml.{Text,XML,Unparsed}
import _root_.net.liftweb._
import http.{S,SHtml}
import common._
import util._
import pl.brosbit.model._
import edu._
import mapper.By
import http.js.JsCmds.SetHtml
import json.JsonDSL._
import json.JsonAST.JObject
import json.JsonParser
import org.bson.types.ObjectId
import Helpers._
import  _root_.net.liftweb.http.js.JsCmds._
import  _root_.net.liftweb.http.js.JsCmd
import  _root_.net.liftweb.http.js.JE._
///import com.sun.org.omg.CORBA._IDLTypeStub

class ShowQuestionSn {
    
    val user = User.currentUser.openOrThrowException("Użytkownik musi być zalogowany!")
    
    def showAllUserQuizes() = {
        val userId = user.id.is
        
        "tr" #> Quiz.findAll("authorId"->userId).map(quiz => {
            <tr><td><a href={"/quiz/"+ quiz._id.toString} target="_blank">{quiz.title}</a></td>
            <td>{quiz.department}</td><td>{quiz.subjectName}</td>
            <td>{quiz.questions.length.toString}</td><td>{quiz.subjectLev.toString}</td>
            <td>{quiz.description}</td><td><a href={"/resources/editquiz/" + quiz._id.toString}>Edytuj</a></td>
            </tr>
        })
    }
    
    def showAllUserQuestions() = {
        val userId = user.id.is
         QuizQuestion.findAll("authorId"->userId).map(quest => {
             <tr><td>{quest.question}</td><td>{quest.answers.mkString(" :: ")}</td><td>{quest.fake.mkString(" :: ")}</td>
             <td>{quest.lev.toString}</td><td>{quest.dificult.toString}</td></tr>
         })
    }
    
   

}
