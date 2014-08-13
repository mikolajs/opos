package pl.brosbit.snippet.edu


import java.io.ByteArrayInputStream
import scala.xml.{ Unparsed}
import _root_.net.liftweb._
import http.{ S, SHtml, FileParamHolder}
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
import mongodb._
import com.mongodb.gridfs._

class FileResourceSn extends BaseResourceSn {
	def showMyFiles = {
	    
	    val idToDel = S.param("del").openOr("")
	    if(idToDel.length > 20){
	        FileResource.find(idToDel) match {
	            case Some(file) if(file.authorId == user.id.is) =>  {
	                 MongoDB.use(DefaultMongoIdentifier) { db =>
	                 val fs = new GridFS(db)
	                 fs.remove(file.fileId)
	                 }
	                file.delete
	            }
	            case _ => 
	        }
	    }
	    
	    
	     "tr" #> FileResource.findAll("authorId" -> user.id.is).map(file => {
	        <tr><td><a href={"/file/" + file.fileId.toString}>{file.title}</a></td>
	        <td>{file.subjectName}</td><td>{file.descript}</td>
	        <td><a href={"/resources/files?del=" + file._id.toString}>Usuń</a></td></tr>
	    })
	}
	
	
	def add = {
	
	    var title = ""
	    var descript = ""
	    var subjectId = ""
	    var mimeType = ""
	    var extension = ""
	    var fileHold: Box[FileParamHolder] = Empty
	    
	    def isCorrect = fileHold match {
          case Full(FileParamHolder(_, mime, _, data))  => {
            mimeType = mime.toString
            true
          }
          case Full(_) => {
            S.error("Nieprawidłowy format pliku!")
            false
          }
          case _ => {
            S.error("Brak pliku?")
            false
          }
        }

        def save():Unit = {
            val user = User.currentUser.openOrThrowException("Niezalogowany nauczyciel")
          
            if (isCorrect) {
                val sub = tryo(subjectId.toLong).openOr(subjectTeach.head.id)
                
                var fileId = "0"
                MongoDB.use(DefaultMongoIdentifier) { db =>
                    val fs = new GridFS(db)
                    val inputFile = fs.createFile(new ByteArrayInputStream(fileHold.get.file))
                    inputFile.setContentType(mimeType)
                    inputFile.setFilename(fileHold.get.fileName)
                    inputFile.save
                    fileId = inputFile.getId().toString()
                }
               
                 if(fileId == "0") return
                val fileRes = FileResource.create(new ObjectId(fileId))
                fileRes.subjectId = sub
                fileRes.subjectName = findSubjectName(sub)
                fileRes.authorId = user.id
                fileRes.title = title
                fileRes.descript = descript
                fileRes.save
                
            }

        }
	    
	        
	    val subjects = subjectTeach.map(sub => (sub.id.toString, sub.name))
	   
	        
	    "#title" #> SHtml.text(title, x => title = x.trim) &
	    "#subjects" #> SHtml.select(subjects, Full(subjects.head._1), subjectId = _) &
	    "#descript" #> SHtml.textarea(descript, x => descript = x.trim) &
	    "#file" #> SHtml.fileUpload(x => fileHold = Full(x)) &
	    "#save" #> SHtml.submit("Dodaj", save)
	    
	}
}