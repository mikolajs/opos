package pl.edu.osp.snippet.edu

import pl.edu.osp.model._
import edu._
import java.util.Date
import scala.xml.{Text, XML, Unparsed}
import _root_.net.liftweb._
import http.{S, SHtml}
import common._
import util._
import mapper.{OrderBy, Descending}
import mapper.By
import json.JsonDSL._
import json.JsonAST.{JObject, JArray, JValue, JBool, JField, JInt}
import json.JsonParser
import org.bson.types.ObjectId
import Helpers._


class HeadWordsSn extends BaseResourceSn {



  def headWordsList() = {

    val headWords = HeadWord.findAll(
      ("authorId" -> user.id.get) ~ ("subjectId" -> subjectId) ~ ("department" -> departName))
    "tbody tr" #> headWords.map(headWord => {
      <tr>
        <td>
          {headWord.title}
        </td>  <td>
        {levMap(headWord.lev.toString)}
      </td>
        <td>
          <a class="btn btn-success" href={"/educontent/editheadword/" + headWord._id.toString}>
            <span class="glyphicon glyphicon-edit"></span>
          </a>
        </td>
      </tr>
    })
  }

  def newLink() = {
    "a [href]" #> ("/educontent/editheadword/0?s=" + subjectId.toString +
      "&d=" + departNr)
  }

  def subjectAndDepartmentChoice() = {
    super.subjectAndDepartmentChoice("/educontent/headwords")
  }

  def subjectChoice() = {
    super.subjectChoice("/educontent/headwords")
  }

}
