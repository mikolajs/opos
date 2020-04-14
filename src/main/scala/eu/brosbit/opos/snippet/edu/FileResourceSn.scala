package eu.brosbit.opos.snippet.edu

import java.io.ByteArrayInputStream
import scala.xml.{Unparsed}
import _root_.net.liftweb._
import http.{S, SHtml, FileParamHolder}
import common._
import util._
import mapper.{OrderBy, Descending}
import eu.brosbit.opos.model._
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

    "tr" #> FileResource.findAll(query)
      .map(file => {
      <tr id={file._id.toString}>
        <td>
          <a href={"/file/" + file.fileId.toString + file.mime}>
            {file.title}
          </a>
        </td>
        <td>
          {file.descript}
        </td>
        <td>
          {levMap(file.lev.toString)}
        </td>
        <td>
          <button class="btn btn-success" onclick="editFile.edit(this);">
            <span class="glyphicon glyphicon-edit"></span>
          </button>
        </td>
      </tr>
    })
  }

  def add = {

    var resourceId = ""
    var title = ""
    var descript = ""
    var level = ""
    var subjectName = ""
    var department = departName
    var mimeType = ""
    var fileName = ""
    var extension = ""
    var fileHold: Box[FileParamHolder] = Empty

    def isCorrect = fileHold match {
      case Full(FileParamHolder(_, mime, fileNameIn, data)) => {
        mimeType = mime.toString
        fileName = fileNameIn
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

    def save(): Unit = {

      val fileRes = FileResource.find(resourceId).getOrElse(FileResource.create)

      var fileId = "0"
      if (isCorrect) {
        MongoDB.use(DefaultMongoIdentifier) {
          db =>
            val fs = new GridFS(db)
            val fileGet = fileHold.openOrThrowException("Not file")
            val inputFile = fs.createFile(new ByteArrayInputStream(
              fileGet.file))

            inputFile.setContentType(mimeType)
            inputFile.setFilename(fileGet.fileName)
            inputFile.save
            fileId = inputFile.getId().toString()
        }
        fileRes.mime = "." + fileName.split('.').last
        fileRes.fileId = new ObjectId(fileId)
      }

      if (fileRes.fileId.toString() != "00000000000000000000") {
        fileRes.subjectId = subjectNow.id
        fileRes.subjectName = subjectNow.name
        fileRes.authorId = user.id.get
        fileRes.lev = tryo(level.toInt).openOr(1)
        fileRes.title = title.trim
        fileRes.descript = descript.trim
        fileRes.department = department
        fileRes.save
      }

    }

    def delete() {
      FileResource.find(resourceId) match {
        case Some(file) if (file.authorId == user.id.get) => {
          MongoDB.use(DefaultMongoIdentifier) {
            db =>
              val fs = new GridFS(db)
              fs.remove(file.fileId)
          }
          file.delete
        }
        case _ =>
      }
    }

    val departs = subjectNow.departments.map(d => (d, d))

    "#resourceId" #> SHtml.text(resourceId, resourceId = _) &
      "#titleAdd" #> SHtml.text(title, title = _) &
      "#subjectAdd" #> SHtml.text(subjectNow.name, subjectName = _, "readonly" -> "readonly") &
      "#levelAdd" #> SHtml.select(levList, Full("1"), level = _) &
      "#descriptAdd" #> SHtml.textarea(descript, descript = _) &
      "#departmentsAdd" #> SHtml.select(departs, Full(department), department = _) &
      "#fileAdd" #> SHtml.fileUpload(x => fileHold = Full(x)) &
      "#deleteAdd" #> SHtml.submit("Usuń", delete) &
      "#saveAdd" #> SHtml.submit("Zapisz", save)

  }

  def subjectChoice() = {
    super.subjectChoice("/educontent/files")
  }

  def subjectAndDepartmentChoice() = {
    super.subjectAndDepartmentChoice("/educontent/files")
  }

}