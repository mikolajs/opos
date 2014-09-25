package pl.brosbit.snippet.edu

import java.util.Date
import scala.xml.{ Text, Unparsed }
import _root_.net.liftweb._
import http.{ S, SHtml }
import common._
import util._
import mapper.{ OrderBy, Descending }
import pl.brosbit.model._
import edu._
import mapper.By
import json.JsonDSL._
import json.JsonAST.JObject
import json.JsonParser
import org.bson.types.ObjectId
import Helpers._

//for show list of all doc and create new doc
class ShowHeadWordSn  {

 
 def showHeadWord() = {
     val id = S.param("id").openOr("0")
     HeadWord.find(id) match {
         case Some(headWord) => {
             "#title *" #> headWord.title &
             "#subject *" #> headWord.subjectName &
             "#level *" #> headWord.lev.toString &
             ".container *" #>  Unparsed("""<h1>%s</h1>""".format(headWord.title) + headWord.content)
         }
         case _ => ".container" #> <h1>Nie znaleziono dokumentu!</h1>
     }     
 }
 

}
