package eu.brosbit.opos.snippet.admin

import com.mongodb.gridfs.GridFS
import net.liftweb.mongodb._
import net.liftweb.util.Helpers._
import net.liftweb.http.S
import org.bson.types.ObjectId
import eu.brosbit.opos.lib.{FileToDelInfo, GCFiles}

import scala.util.Try

class AdminGC {
  val del = S.param("del").getOrElse("0")
  val page = S.param("p").getOrElse("1").toInt


  def allImages() = {
    val GC = new GCFiles()

    if(del.size == 24) delete()
    if(del == "all") {
      delAll(GC.getAllToDelete())
    }

    val files = GC.getAllToDelete()
    println("FILES TO DELETE IN ADMIN GC: " + files.size.toString)
    //files.foreach(ftod => println(ftod.id + " " + ftod.fileType))
    val filesNotDel = GC.getAllToDelete()
    ".center *" #> files.map(ftod => {
      val l = createLink(ftod)
      ".toDeleteFile" #> <a href={l}>
        {l}
      </a> &
        "a [href]" #> ("/admin/gc?del=" + ftod.id)
    })
  }

  private def createLink(ftod:FileToDelInfo):String = {
    if(ftod.fileType == "jpg" || ftod.fileType == "jpeg" || ftod.fileType == "png"
    || ftod.fileType == "gif") s"/img/${ftod.id}.${ftod.fileType}"
    else s"/file/${ftod.id}.${ftod.fileType}"
  }

  def delete() = MongoDB.use(DefaultMongoIdentifier) { db =>
      val fs = new GridFS(db)
      fs.remove(new ObjectId(del))

  }

  def delAll(files: List[FileToDelInfo]): Unit = {
    println("START DELETE ALL FILES")
    MongoDB.use(DefaultMongoIdentifier) { db =>
      val fs = new GridFS(db)
      files.foreach(f =>
        if(f.id.size == 24) Try(fs.remove(new ObjectId(f.id)))
      )
    }
  }




}
