package pl.brosbit.snippet.edu

import java.util.Date
import scala.xml.{Text,XML,Unparsed}
import _root_.net.liftweb._
import http.{S,SHtml}
import common._
import util._
import mapper.{OrderBy,Descending}
import pl.brosbit.model._
import edu._
import mapper.By
import json.JsonDSL._
import json.JsonAST.{JObject, JArray, JValue, JBool, JField, JInt}
import json.JsonParser
import org.bson.types.ObjectId
import Helpers._


class SlidesSn extends  BaseResourceSn  {
     

  def slidesList() = {
    val idUser =user.id.is 
    val slides = Slide.findAll(("authorId"-> idUser))
    "tbody tr" #> slides.map(slide => {
        <tr><td><a href={"/showslide/"+slide._id.toString} target="_blank">{slide.title}</a></td>
    	<td>{slide.descript}</td>
    	<td><a href={"/educontent/editslide/"+slide._id.toString}>
    		<span class="glyphicon glyphicon-edit"></span></a> 
    	</td></tr>
    })
  }  
  
}