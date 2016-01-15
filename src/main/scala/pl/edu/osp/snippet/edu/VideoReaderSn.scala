package pl.edu.osp.snippet.edu

import java.io.File
import java.nio.file.{Files, Path, Paths, StandardCopyOption}
import scala.xml.{Unparsed, Text, NodeSeq}
import _root_.net.liftweb._
import http.{S, SHtml, FileParamHolder}
import common._
import util._
import mapper.{OrderBy, Descending}
import pl.edu.osp.model._
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
  println("start indexvideo")
  val parentPath = "/home/"
  val parentDir = new File(parentPath)
  val userDirPath = parentPath + toASCIICharAndLower(user.firstName.is) + user.id.toString
  // parentDir.listFiles()

  def getUserNames() = parentDir.listFiles().filter(f => f.isDirectory()).map(f => f.getName())

  def getPaths() = {
    println("getPaths")
    val userDir = new File(userDirPath)
    val paths: List[File] = findFiles(userDir, Nil)
    paths.map(f => {
      val full = f.getAbsolutePath()
      val mime = tryo(Files.probeContentType(Paths.get(full))).getOrElse("error")
      println(full)
      (full, mime)
    })
  }


  def findFiles(dir: File, list: List[File]): List[File] = {
    println("findFiles")
    if (dir.isDirectory()) {
      val allFilArray = dir.listFiles()
      if (allFilArray != null) {
        val allFil = allFilArray.toList
        val mapped = allFil.map(f => findFiles(f, list)).flatten
        mapped
      } else Nil
    } else {
      val name = dir.getName()
      if (isVideo(name)) dir :: list
      else Nil
    }

  }

  def indexed = {
    println("indexed")
    //add filter not added
    val existPath = Video.findAll(("authorId" -> user.id.get) ~ ("onServer" -> true))
      .map(v => userDirPath + v.oldPath)
    val videos = getPaths().filterNot(p => existPath.exists(e => e == p._1))
      .map(p => (p._1.replace(userDirPath, ""), p._2))
    "li" #> videos.map(v => <li onclick="insertToAdd(this)" class="list-group-item">
      <strong>
        {v._1}
      </strong>
      -
      <em>
        {v._2}
      </em>
    </li>)
  }

  def add() = {
    var path = ""
    var level = "1"
    var department = ""
    var title = ""
    var descript = ""
    var mime = ""
    def copy() {
      val videosOnDB = Video.findAll(("authorId" -> user.id.get) ~ ("onServer" -> true) ~ ("oldPath" -> path))
      if (videosOnDB.isEmpty) {
        println("IS EMPTY")

        val video = Video.create
        video.authorId = user.id.get
        video.department = department
        video.title = title
        video.link = path.split("/").last
        video.oldPath = path
        video.lev = tryo(level.toInt).openOr(1)
        video.onServer = true
        video.mime = mime
        video.descript = descript
        video.subjectId = subjectNow.id
        video.subjectName = subjectNow.name
        val file = new File(userDirPath + path)
        if (file.isFile()) {
          val newFile = new File("/home/osp/" + video._id.toString + "." + video.link.split('.').last)
          Files.copy(file.toPath(), newFile.toPath())
          video.save
        }
      }
    }

    val departments = subjectNow.departments.map(d => (d, d))

    "#title" #> SHtml.text(title, title = _) &
      "#mime" #> SHtml.text(mime, mime = _, "readonly" -> "readonly") &
      "#path" #> SHtml.text(path, path = _, "readonly" -> "readonly") &
      "#subject" #> SHtml.text(subjectNow.name, x => Unit, "readonly" -> "readonly") &
      "#level" #> SHtml.select(levList, Full(subjectNow.lev.toString), level = _) &
      "#description" #> SHtml.textarea(descript, descript = _) &
      "#department" #> SHtml.select(departments, Full(department), department = _) &
      "#move" #> SHtml.button(<span class="glyphicon glyphicon-export"></span> ++ Text(" Dodaj plik"), copy)
  }

  private def isVideo(name: String) = {
    println("start isVideo method")
    name.split('.').last.toLowerCase() match {
      case "mp4" => true
      case _ => false
    }
  }

  private def toASCIICharAndLower(str: String) = {
    println("toASCIICharLower")
    val m = Map(('Ą', 'A'), ('Ć', 'C'), ('Ę', 'E'), ('Ł', 'L'), ('Ń', 'N'), ('Ó', 'O'), ('Ś', 'S'), ('Ź', 'Z'), ('Ż', 'Z'),
      ('ą', 'a'), ('ć', 'c'), ('ę', 'e'), ('ł', 'l'), ('ń', 'n'), ('ó', 'o'), ('ś', 's'), ('ź', 'z'), ('ż', 'z'),
      (' ', '-'))
    str.toLowerCase.toCharArray().map(n =>
      if (m.contains(n)) m(n) else n
    ).mkString
  }
}
