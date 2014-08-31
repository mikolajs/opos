package pl.brosbit.snippet.edu

import java.io.{ByteArrayInputStream, File, FileOutputStream}
import scala.xml.{ Unparsed, Text, NodeSeq }
import _root_.net.liftweb._
import http.{ S, SHtml, FileParamHolder }
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

class VideoSn extends BaseResourceSn {

  def showVideos = {
    val idToDel = S.param("del").openOr("")
    if (idToDel.length > 20) {
      Video.find(idToDel) match {
        case Some(video) if (video.authorId == user.id.is) => {
          if(video.onServer) {
            val file = new File("/home/vregister/" + video._id.toString + "." + video.link.split('.').last)
            if(file.exists() && file.isFile()) try {
              file.delete()
            } catch {case _:Throwable => S.notice("Brak pliku do usunięcia") }
          }
          video.delete
        }
        case _ =>
      }
    }

    "tr" #> Video.findAll("authorId" -> user.id.is).map(video => {
      <tr id={ video._id.toString }>
        <td><a href={ if (video.onServer) 
        	"http://video.epodrecznik.edu.pl/" + video._id.toString + "." + video.link.split('.').last 
        	else "http://youtube.com/embed/" + video.link } target="_blank">{ video.title }</a></td>
        <td>{
          if (video.onServer) <img src="/style/images/video.png"/>
          else <img src="/style/images/youtube.png"/>
        }</td>
        <td>{ video.descript }</td><td>{ video.subjectName }</td>
        <td>{video.department}</td>
        <td>
          <a href={ "/educontent/video?del=" + video._id.toString } 
          	onclick="return editVideo.sureDel();" class="btn btn-danger">Usuń</a>
          <span class="btn btn-info" onclick="editVideo.edit(this);">Edytuj</span>
        </td>
      </tr>
    })
  }

  def add = {

    var title = ""
    var descript = ""
    var subjectId = ""
    var onserver = false
    var linkTube = ""
    var videoId = ""
    var mimeType = ""
    var fileName = ""
    var depart = ""


    def save(): Unit = {

      val sub = tryo(subjectId.toLong).openOr(subjectTeach.head.id)
      val video = if (videoId.length < 20) Video.create else Video.find(videoId).getOrElse(Video.create)
      if(!onserver) {
        video.link = linkTube
        video.oldPath = ""
      }
      video.onServer = onserver
      video.subjectId = sub
      video.subjectName = findSubjectName(sub)
      video.title = title
      video.department = depart
      video.descript = descript
      video.authorId = user.id.is
      video.save
    }

    val subjects = subjectTeach.map(sub => (sub.id.toString, sub.name))

      "#videoId" #> SHtml.text(videoId, videoId = _) &
      "#title" #> SHtml.text(title, x => title = x.trim) &
      "#subjects" #> SHtml.select(subjects, Full(subjects.head._1), subjectId = _) &
      "#department" #> SHtml.text(depart, depart = _) &
      "#onserver" #> SHtml.checkbox_id(onserver, (x: Boolean) => onserver = x, Full("onserver")) &
      "#descript" #> SHtml.textarea(descript, x => descript = x.trim) &
      "#linkTube" #> SHtml.text(linkTube, x => linkTube = x.trim) &
      "#save" #> SHtml.submit("Dodaj", save)

  }
  
 //override  def autocompliteScript(in:NodeSeq) = super.autocompliteScript(in)
}
