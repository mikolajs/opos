package eu.brosbit.opos.api

import net.liftweb._
import common._
import util._
import http._
import com.mongodb.gridfs._
import net.liftweb.mongodb._
import java.io.{ByteArrayInputStream, ByteArrayOutputStream, File}

import provider.servlet.HTTPServletContext
import com.mongodb.DBObject
import _root_.net.liftweb.mongodb.DefaultMongoIdentifier
import eu.brosbit.opos.model.User

object FileLoader {


  def file(idWithExtension: String): Box[LiftResponse] = {
    val id = idWithExtension.split('.').head
    val outputStream = new ByteArrayOutputStream()
    var mime = ""
    var fileName = ""
    MongoDB.use(DefaultMongoIdentifier) {
      db =>
        val fs = new GridFS(db)
        val foundFile = fs.findOne(new org.bson.types.ObjectId(id))
        if (foundFile == null) println("++!!!!!!!!!!!!!!!!!!!!!!  file is null " + id + " !!!!!!!!!!!!!!!!!!!!!!!!!")
        else {
          foundFile.writeTo(outputStream)
          fileName = foundFile.getFilename
          mime = foundFile.getFilename().split('.').last
        }
    }
    val inputStream = new ByteArrayInputStream(outputStream.toByteArray)
    if (inputStream.available() < 10) {
      Full(NotFoundResponse("Not found"))
    }
    else {
      val headerFile = ("Content-Disposition", "form-data; filename=\"" + fileName + "\"")
      val headerMain = ("Content-Type", "file/" + mime)
      val headers = if(fileName.isEmpty) headerMain :: Nil else headerMain :: headerFile :: Nil
      Full(StreamingResponse(inputStream, () => (), inputStream.available().toLong, headers, Nil, 200))
    }
  }


}