package pl.brosbit.snippet.edu

import pl.brosbit.model._
import edu._
import java.util.Date
import scala.xml.{Text,XML,Unparsed}
import 	_root_.net.liftweb._
import http.{S,SHtml}
import common._
import util._
import mapper.{OrderBy,Descending}
import mapper.By
import json.JsonDSL._
import json.JsonAST.{JObject, JArray, JValue, JBool, JField, JInt}
import json.JsonParser
import org.bson.types.ObjectId
import Helpers._


class HeadWordsSn extends   BaseResourceSn {
  
    val subjPar = S.param("s").openOr("")
    val levPar = S.param("l").openOr("3")
    val subjectId = if(subjPar != "") tryo(subjPar.toLong).openOr(subjectTeach.head.id)
    								else subjectTeach.head.id
  
    println("============== " + subjPar + " ========= " + levPar)
  def headWordsList() = {
   
    val headWords = HeadWord.findAll(("authorId"-> user.id.is)~("subjectLev"->levPar.toInt)~("subjectId"->subjectId))
    "tbody tr" #>headWords.map(headWord => {
        val edit_? = (headWord.authorId == user.id.is || user.role == "a") 
        <tr><td><a href={"/headword/"+headWord._id.toString} target="_blank">{headWord.title}</a></td>
       	<td> </td>
    	<td>{if(edit_?)
    	<a href={"/resources/editheadword/"+headWord._id.toString}>edytuj</a> else <i></i>}</td></tr>
    })
  }
  
  def selectors() = {
    val subj = subjectTeach.map(s => (s.id.toString,  s.name))
    "#subjectSelect" #>SHtml.select(subj, Full(subjPar) , x => Unit) &
    "#levelSelect" #> SHtml.select(levList, Full(levPar) , x => Unit)
    
  }
  
}
