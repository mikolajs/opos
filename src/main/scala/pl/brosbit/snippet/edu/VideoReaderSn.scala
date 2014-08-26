package pl.brosbit.snippet.edu

import java.io.File
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

//case class VideoItem(depart:String, path:String)

class VideoReaderSn extends BaseResourceSn {

  var videoItems: scala.collection.mutable.ListBuffer[File] = scala.collection.mutable.ListBuffer()

  val parentPath = "/home/"
  val parentDir = new File(parentPath)
  // parentDir.listFiles()

  def getUserNames() = parentDir.listFiles().filter(f => f.isDirectory()).map(f => f.getName())

  def getPaths(userName: String) = {

    val userDirPath = parentPath + "/" + userName + "/Video"
    val userDir = new File(userDirPath)
    var paths: List[File] = findFiles(userDir, Nil)
    paths.map(f => f.getAbsolutePath())
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
    var name = user.firstName.is
    val paths = getPaths(name)
    val userDirPath = parentPath + "/" + name + "/Video"
    val videos = paths.map(p => p.replace(userDirPath, "")).map(p => {
      val pathArray = p.split("/")
      if (pathArray.length > 1) (pathArray.head, pathArray.last.split('.').head)
      else ("","")
    }).filter(t => t._1 == "")
    "li" #> videos.map(v => <li class="list-group-item"><strong>{v._2}</strong> - <em>{v._1}</em></li>)
  }

  def add() = {
	var subjectId = ""
	var data = ""
    def copy() {
	  
	}
    S.redirectTo("/educontent/video")
    
    "#subject" #> SHtml.select(subjectTeach.map(s => (s._id.toString(), s.name )), 
        Full(subjectTeach.head._id.toString), subjectId = _) &
        "#data" #> SHtml.text(data, data = _) &
        "#move" #>  SHtml.button(<span class="glyphicon glyphicon-export"></span>++ Text(" Eksportuj"), copy)
  }

  private def isVideo(name: String) = {
    name.split('.').last match {
      case "avi" => true
      case "mp4" => true
      case "mpeg" => true
      case _ => false
    }
  }
}