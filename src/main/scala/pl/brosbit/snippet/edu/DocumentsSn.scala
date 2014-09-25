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

//for show list of all doc for teacher
class DocumentsSn  extends BaseResourceSn {
//edocuments => show edocuments list 
    
    
 def docList() = {
   
    val documents = Document.findAll(("authorId" -> user.id.is)~("subjectId" -> subjectNow.id)
        ~("lev" -> tryo(levStr.toInt).openOr(1)))
    "tbody tr" #> documents.map(doc => ".titleTd *" #>  <strong>{doc.title}  </strong> &
      ".descriptTd *" #> Text( doc.descript ) &
      ".departmentTd *" #> Text(doc.department) &
      ".editTd *" #> <a class="btn btn-success" href={"/educontent/editdocument/" + doc._id.toString}> Edytuj</a>
      )
  }


def choiceSubjectAndLevel() = {
    super.choiceSubjectAndLevel("/educontent/documents")
  }

}
