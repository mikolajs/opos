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
   val userDirPath = parentPath + toASCIICharAndLower(user.firstName.is) + user.id.toString
  // parentDir.listFiles()

  def getUserNames() = parentDir.listFiles().filter(f => f.isDirectory()).map(f => f.getName())

  def getPaths() = {
   
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
    //add filter not added
    val videos = getPaths().map(p => (p._1.replace(userDirPath, ""), p._2))
    "li" #> videos.map(v => <li onclick="insertToAdd(this)" class="list-group-item">
    			<strong>{v._1}</strong> - <em>{v._2}</em></li>)
  }

  def add() = {
	var subjectId = ""
	var path = ""
    var level = ""
    var department = ""
    var title = ""
    var mime = ""
    def copy() {
     val videosOnDB = Video.findAll(("authorId" -> user.id.is)~("onServer" -> true)~("oldPath" -> path))
	 if(videosOnDB.isEmpty) {
	   val video = Video.create
	   video.authorId = user.id.is
	   video.department = department
	   video.title = title
	   video.link = path.split("/").last
	   video.oldPath = path
	   video.onServer = true
	   video.mime = mime
	   video.subjectId = tryo(subjectId.toLong).openOr(subjectTeach.head.id)
	   video.subjectName = findSubjectName(video.subjectId)
	   val file = new File(path)
	   if(file.isFile()){
	     val newFile = new File("/home/vregister/" + video._id.toString + "." + video.link.split('.').last)
	     Files.copy(file.toPath(), newFile.toPath())
	     video.save
	   }
	 }
	}
	
	val departments = subjectNow.departments.map( d => (d, d))

    "#title" #> SHtml.text(title, title = _) &
    "#mime" #> SHtml.text(mime, mime = _ , "readonly" -> "readonly") &
    "#subject" #> SHtml.text(subjectNow.name, x => Unit, "readonly" -> "readonly") &
    "#level" #> SHtml.select(levList, Full(subjectNow.lev.toString), level = _) &
    "#department" #> SHtml.select(departments, Full(department), department = _) &
    "#move" #>  SHtml.button(<span class="glyphicon glyphicon-export"></span>++ Text(" Dodaj plik"), copy)
  }

  private def isVideo(name: String) = {
    name.split('.').last.toLowerCase() match {
      //case "avi" => true
      case "mp4" => true
      //case "mpg" => true
      //case "ogg" => true
      case _ => false
    }
  }
  
  private def addDepartment(dep:String, subject:SubjectTeach) {
    if(!subject.departments.contains(dep)) subject.departments = dep::subject.departments
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
    val paths = getPaths()
    val videos = paths.map(p => (p._1.replace(userDirPath, ""), p._2, p._1)).map(p => {
      val pathArray = p._1.split("/")
      if (pathArray.length > 1) (pathArray.head, pathArray.last, p._2, p._3)
      else ("","", "", "")
    }).filter(t => t._2 != "")
    videos
  }
}
