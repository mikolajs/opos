package eu.brosbit.opos.snippet.edu

import java.util.Date
import scala.xml.{Text, XML, Unparsed}
import _root_.net.liftweb._
import net.liftweb.http.{FileParamHolder, S, SHtml}
import common._
import util._
import mapper.{OrderBy, Descending}
import eu.brosbit.opos.model._
import edu._
import mapper.By
import json.JsonDSL._
import json.JsonAST.{JObject, JArray, JValue, JBool, JField, JInt}
import json.JsonParser
import org.bson.types.ObjectId
import Helpers._
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import java.io.{ByteArrayOutputStream, ByteArrayInputStream}
import net.liftweb.mongodb._
import net.liftweb.common.Full
import com.mongodb.gridfs.GridFS
import eu.brosbit.opos.api.ImportExportSlides


class SlidesSn extends BaseResourceSn {


  def slidesList() = {
    val idUser = user.id.get
    val slides = Presentation.findAll(("authorId" -> idUser))
    "tbody tr" #> slides.map(slide => {
      <tr>
        <td>
          <a href={"/showslide/" + slide._id.toString} target="_blank">
            {slide.title}
          </a>
        </td>
        <td>
          {slide.descript}
        </td>
        <td>
          <a href={"/educontent/editslide/" + slide._id.toString}>
            <span class="glyphicon glyphicon-edit"></span>
          </a>
        </td>
      </tr>
    })
  }


  //plik zip sprawdzić tylko rozszerzenie lub mime type z
  def uploadSlides() = {
    var fileHold: Box[FileParamHolder] = Empty
    var infoStr = ""
    def load() {
      if (ImportExportSlides.loadZip(fileHold))
        infoStr = "wczytanie udane"
      else
        infoStr = "Nieudane wczytywanie!"
    }
    "#zip" #> SHtml.fileUpload(fileUploaded => fileHold = Full(fileUploaded)) &
    "info *" #> infoStr &
    "#addZip" #> SHtml.submit("Importuj", load)
  }


}