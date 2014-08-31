package pl.brosbit.snippet.edu

import java.io.File
import java.nio.file.{Files, Path, Paths, StandardCopyOption}
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
import java.nio.file.Path

//case class VideoItem(depart:String, path:String)

class VideoReaderSn extends BaseResourceSn {

  var videoItems: scala.collection.mutable.ListBuffer[File] = scala.collection.mutable.ListBuffer()

  val parentPath = "/home/"
  val parentDir = new File(parentPath)
  // parentDir.listFiles()

  def getUserNames() = parentDir.listFiles().filter(f => f.isDirectory()).map(f => f.getName())

  def getPaths(userName: String) = {

    val userDirPath = parentPath + userName + "/Video"
    val userDir = new File(userDirPath)
    var paths: List[File] = findFiles(userDir, Nil)
    paths.map(f => {
      val full = f.getAbsolutePath()
      val mime = Files.probeContentType(Paths.get(full))
      (full, mime)
      })
  }
  
  def findFiles(dir: File, list: List[File]): List[File] = {

    if (dir.isDirectory()) {
      val allFil = dir.listFiles().toList
      val maped = allFil.map(f => findFiles(f, list)).flatten
      maped
    } else {
      val name = dir.getName()
      if (isVideo(name)) dir :: list
      else Nil
    }

  }
  
  def indexed = {
    val videos = findAllVideoInUserPath
    "li" #> videos.map(v => <li class="list-group-item"><strong>{v._2}</strong> - <em>{v._1}</em></li>)
  }

  def add() = {
	var subjectId = ""
	var data = ""
    def copy() {
	 val videosOnPath = findAllVideoInUserPath
     val videosOnDB = Video.findAll(("authorId" -> user.id.is)~("onServer" -> true)).map(vDB => vDB.oldPath)
	 val videosNotAdded = videosOnPath.filterNot(vOP => videosOnDB.exists(vODB => vODB == (vOP._1 + "/" + vOP._2)))
	 videosNotAdded.foreach(v => {
	   val video = Video.create
	   video.authorId = user.id.is
	   video.department = v._1
	   video.title = v._2.split('.').head
	   video.link = v._2
	   video.oldPath = v._1 + "/" + v._2
	   video.onServer = true
	   video.mime = v._3
	   video.subjectId = tryo(subjectId.toLong).openOr(subjectTeach.head.id)
	   video.subjectName = findSubjectName(video.subjectId)
	   val file = new File(v._4)
	   if(file.isFile()){
	     val newFile = new File("/home/vregister/" + video._id.toString + "." + video.link.split('.').last)
	     Files.copy(file.toPath(), newFile.toPath())
	     video.save
	   }

	 })
     println("\nWIDEOPATH:::: \n" + videosOnPath.map(v => v._1 + "/" +  v._2).mkString("\n"))
     println("\nVIDEODB:::::  \n" + videosOnDB.mkString("\n"))
     println("\nVIDEONOTADDED:::::: \n" + videosOnPath.map(v => v._1 + "/" +  v._2).mkString("\n"))
	  S.redirectTo("/educontent/video")
	}

    
    "#subject" #> SHtml.select(subjectTeach.map(s => (s._id.toString(), s.name )), 
        Full(subjectTeach.head.id.toString), subjectId = _) &
    "#move" #>  SHtml.button(<span class="glyphicon glyphicon-export"></span>++ Text(" Eksportuj"), copy)
  }

  private def isVideo(name: String) = {
    name.split('.').last.toLowerCase() match {
      case "avi" => true
      case "mp4" => true
      case "mpg" => true
      case "ogg" => true
      case _ => false
    }
  }
  
  private def toASCIICharAndLower(str:String) = {
    val m = Map(( 'Ą', 'A' ), ( 'Ć', 'C' ), ( 'Ę', 'E' ), ( 'Ł', 'L' ), ( 'Ń', 'N' ), ( 'Ó', 'O' ), ( 'Ś', 'S' ), ( 'Ź', 'Z' ), ( 'Ż', 'Z' ),
        ( 'ą', 'a' ), ( 'ć', 'c' ), ( 'ę', 'e' ), ( 'ł', 'l' ), ( 'ń', 'n' ), ( 'ó', 'o' ), ( 'ś', 's' ), ( 'ź', 'z' ), ( 'ż', 'z' ),
        ( ' ', '-' ))
        str.toLowerCase.toCharArray().map(n => 
          if(m.contains(n)) m(n) else n
          ).mkString
  }
  
  private def findAllVideoInUserPath = {
    var name = toASCIICharAndLower(user.firstName.is) + user.id.toString
    val paths = getPaths(name)
    val userDirPath = parentPath + name + "/Video/"
    val videos = paths.map(p => (p._1.replace(userDirPath, ""), p._2, p._1)).map(p => {
      val pathArray = p._1.split("/")
      if (pathArray.length > 1) (pathArray.head, pathArray.last, p._2, p._3)
      else ("","", "", "")
    }).filter(t => t._2 != "")
    videos
  }
}