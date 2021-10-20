package eu.brosbit.opos.snippet.edu

import _root_.net.liftweb._
import util._
import eu.brosbit.opos.model.edu.{Slide}
import json.JsonDSL._
import Helpers._


class PresentationsSn extends BaseResourceSn {

  def slidesList() = {

    val slides = Slide.findAll(query)
    "tbody tr" #> slides.map(slide => {
      <tr>
        <td>
         <a href={"/showslide/" + slide._id.toString} target="_blank"> {slide.title} </a>
        </td> <td> {slide.department}</td>
        <td>
        {levMap(slide.lev.toString)}
      </td>
        <td>
          <a class="btn btn-success" href={"/educontent/editpresentation/" + slide._id.toString}>
            <span class="glyphicon glyphicon-edit"></span>
          </a>
        </td>
      </tr>
    })
  }

  def newLink() = {
    "a [href]" #> ("/educontent/editpresentation/0?s=" + subjectId.toString +
      "&d=" + departNr)
  }

  def subjectAndDepartmentChoice() = {
    super.subjectAndDepartmentChoice("/educontent/presentations")
  }

  def subjectChoice() = {
    super.subjectChoice("/educontent/presentations")
  }

}

