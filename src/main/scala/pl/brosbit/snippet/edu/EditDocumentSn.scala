package pl.brosbit.snippet.edu

import java.util.Date
import scala.xml.{ Text, XML, Unparsed, Elem, Null, TopScope }
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
import http.js.{JsCmds, JsCmd}
import http.js.JsCmds.{ SetHtml, Alert, Run}
import http.js.JE._

class EditDocumentSn extends BaseResourceSn {
    
    val userId = user.id.is
    
    val id = S.param("id").openOr("0")
    var document = if (id != "0") Document.find(id).getOrElse(Document.create) else Document.create
    
    
    val isOwner = 
        document.ownerID == 0L || document.ownerID == userId || User.currentUser.openOrThrowException("Niezalogowany nauczyciel").role == "a"

    def editData() = {
    	
        var docID = id
        var title = document.title
        var descript = document.descript
        var subjectId = document.subjectId.toString
        var level = document.level.toString
        var docContent = document.content

        def save() {
            //println("-------------save------------title-----------------")
            if (isOwner) {
                document.title = title
                document.descript = descript
                        document.subjectId = tryo(subjectId.toLong).openOr(0L)
                        document.level = level.toInt
                        document.subcjectName = findSubjectName(document.subjectId)
                        document.ownerID = user.id.is
                        document.ownerName = user.getFullName
                        document.content = docContent
                        document.save                       
                        if (id == "0") S.redirectTo("/resources/editdocument/"+ document._id.toString)
                        Alert("Zapisano")
                    
            } 
        }
        
        def delete() = {
            if(isOwner) {
                document.delete
            }
        }

        val subjects = this.subjectTeach.map(sub => (sub.id.toString,  sub.name))
        "#docID" #> SHtml.text(docID, docID = _) &
            "#docTitle" #> SHtml.text(title, title = _) &
            "#docDescription" #> SHtml.textarea(descript, descript = _) &
            "#subjects" #> SHtml.select(subjects, Full(subjectId), subjectId = _) &
            "#docLevel" #> SHtml.select(levList, Full(level), level = _) &
            "#docContent" #> SHtml.textarea(docContent, d => docContent =  d.trim) & 
            "#docSave" #> SHtml.submit("Zapisz", save) &
            "#docDelete" #> SHtml.submit("Usuń", delete)
    }
 

}
