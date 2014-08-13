package pl.brosbit.snippet.edu

import pl.brosbit._
import model._
import edu._
import _root_.net.liftweb._
import common._
import util._
import http.{S,SHtml}
import json.JsonDSL._
import json.JsonAST.JObject
import json.JsonParser
import org.bson.types.ObjectId
import Helpers._
import Helpers._

class BaseResourceSn {

    val user = User.currentUser.openOrThrowException("Niezalogowany nauczyciel")
    val subjectTeach = SubjectTeach.findAll("authorId"-> user.id.is)
    if(subjectTeach.isEmpty) S.redirectTo("/resources/options")
    
   val levList = List(("1","podstawowy"),("2","średni"),("3","rozszerzony"))
   
   def  techerSubjects() = {
    val subj = subjectTeach.map(s => (s.id, s.name))
    "#subjectSelect" #> subj.map(s => 
        "option" #> <option value={s._1.toString}>{s._2}</option>)
  }
   
   def  findSubjectName(id:Long):String = {
       for(s <- subjectTeach){
           if(s.id == id) return s.name
       }
       ""
   }

   
   
     
  
}