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

class VideoSn  extends BaseResourceSn {
	def showVideos = {
	    
	    val idToDel = S.param("del").openOr("")
	    if(idToDel.length > 20){
	        Video.find(idToDel) match {
	            case Some(video) if(video.authorId == user.id.is) =>  {
	                video.delete
	            }
	            case _ => 
	        }
	    }
	    
	    
	     "tr" #> Video.findAll("authorId" -> user.id.is).map(video => {
	        <tr><td><a href={"http://youtube.com/embed/" + video.link} target="_blank">{video.title}</a></td>
	        <td>{if(video.onServer) "serwer" else "YpuTube"}</td><td>{video.descript}</td><td>{video.subjectName}</td>
	        <td><a href={"/resources/video?del=" + video._id.toString}>Usuń</a></td></tr>
	    })
	}
	
	
	def add = {
	
	    var title = ""
	    var descript = ""
	    var subjectId = ""
	    var onserver = false
	    var link = ""
	    

        def save():Unit = {
          
            val sub = tryo(subjectId.toLong).openOr(subjectTeach.head.id)
              
                val video = Video.create
                video.link =  link
                video.onServer = onserver
                video.subjectId = sub
                video.subjectName = findSubjectName(sub)
                video.title = title
                video.descript = descript
                video.save

        }
	    
	        
	    val subjects = subjectTeach.map(sub => (sub.id.toString, sub.name))
	   
	        
	    "#title" #> SHtml.text(title, x => title = x.trim) &
	    "#subjects" #> SHtml.select(subjects, Full(subjects.head._1), subjectId = _) &
	    "#onserver" #> SHtml.checkbox(onserver, onserver = _) &
	    "#descript" #> SHtml.textarea(descript, x => descript = x.trim) &
	    "#link" #> SHtml.text(link, x => link = x.trim) &
	    "#save" #> SHtml.submit("Dodaj", save)
	    
	}
}