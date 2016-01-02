package pl.brosbit.snippet.edu

import java.io.{ByteArrayInputStream, File, FileOutputStream}
import scala.xml.{Unparsed, Text, NodeSeq}
import _root_.net.liftweb._
import http.{S, SHtml, FileParamHolder}
import common._
import util._
import mapper.{OrderBy, Descending}
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
   val pathVideo = S.hostAndPath.split('/').take(3).mkString("/").split(':').take(2).mkString(":") + "/osp/"
   //println("+++++++++++++++++ hostName Video: " + pathVideo)

    "tr" #> Video.findAll(("authorId" -> user.id.get) ~ ("subjectId" -> subjectNow.id)
    ).map(video => {
      <tr id={video._id.toString}>
        <td>
          <a href={if (video.onServer)
            pathVideo + video._id.toString + "." + video.link.split('.').last
          else "http://youtube.com/embed/" + video.link} target="_blank">
            {video.title}
          </a>
        </td>
        <td>
          {if (video.onServer) <img src="/style/images/video.png"/>
        else <img src="/style/images/youtube.png"/>}
        </td>
        <td>
          {video.descript}
        </td>
        <td>
          {video.department}
        </td>
        <td>
          {levMap(video.lev.toString)}
        </td>
        <td>
          <button class="btn btn-success" onclick="editVideo.edit(this);">
            <span class="glyphicon glyphicon-edit"></span>
          </button>
        </td>
      </tr>
    })
  }


  def add = {

    var title = ""
    var descript = ""
    var subjectName = ""
    var onserver = false
    var linkTube = ""
    var videoId = ""
    var mimeType = ""
    var fileName = ""
    var depart = ""
    var level = ""


    def save(): Unit = {

      val video = if (videoId.length < 20) Video.create else Video.find(videoId).getOrElse(Video.create)

      if (video.authorId != 0L && video.authorId != user.id.get) return

      if (!onserver) {
        video.link = linkTube
        video.oldPath = ""
      }
      video.onServer = onserver
      video.subjectId = findSubjectId(subjectName)
      video.subjectName = subjectName
      //val levId = levList.find(l => l._2 == level).getOrElse(levList.head)._1
      video.lev = tryo(level.toInt).openOr(1)
      video.title = title.replace( '\'','`')
      video.department = depart
      video.descript = descript
      video.authorId = user.id.get
      video.save
    }

    def delete() {
      Video.find(videoId) match {
        case Some(video) if (video.authorId == user.id.get) => {
          if (video.onServer) {
            val file = new File("/home/vregister/" + video._id.toString + "." + video.link.split('.').last)
            if (file.exists() && file.isFile()) try {
              file.delete()
            } catch {
              case _: Throwable => S.notice("Brak pliku do usunięcia")
            }
          }
          video.delete
        }
        case _ =>
      }
    }

    val departs = subjectNow.departments.map(d => (d, d))

    "#videoId" #> SHtml.text(videoId, videoId = _) &
      "#titleAdd" #> SHtml.text(title, x => title = x.trim) &
      "#subjectAdd" #> SHtml.text(subjectNow.name, subjectName = _, "readonly" -> "readonly") &
      "#levelAdd" #> SHtml.select(levList, Full("1"), level = _) &
      "#departmentsAdd" #> SHtml.select(departs, Full(depart), depart = _) &
      "#onserver" #> SHtml.checkbox_id(onserver, (x: Boolean) => onserver = x, Full("onserver")) &
      "#descriptAdd" #> SHtml.textarea(descript, x => descript = x.trim) &
      "#linkTube" #> SHtml.text(linkTube, x => linkTube = x.trim) &
      "#deleteAdd" #> SHtml.submit("Usuń", delete) &
      "#saveAdd" #> SHtml.submit("Zapisz", save)

  }

  def fromServer() = {
    "a [href]" #> ("/educontent/indexvideo?s=" + subjectNow.id.toString)
  }

  def subjectChoice() = {
    super.subjectChoice("/educontent/video")
  }

  //override  def autocompliteScript(in:NodeSeq) = super.autocompliteScript(in)
}
