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


class HeadWordsSn extends BaseResourceSn {
  

    val subjectId = subjectNow.id

  def headWordsList() = {
   
    val headWords = HeadWord.findAll(("authorId"-> user.id.is)~("subjectId"->subjectId))
    "tbody tr" #>headWords.map(headWord => {
        <tr><td>{headWord.title}</td><td>{headWord.department}</td><td>{levMap(headWord.lev.toString)}</td>
    	<td><a  class="btn btn-success" href={"/educontent/editheadword/"+headWord._id.toString}>
    	<span class="glyphicon glyphicon-edit"></span></a></td></tr>
    })
  }
  
  def newLink() = {
     "a [href]" #> ("/educontent/editheadword/0?s=" + subjectId.toString)
  }
  
  def subjectChoice() = {
    super.subjectChoice("/educontent/headwords")
  }
  
}
