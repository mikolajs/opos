package eu.brosbit.opos.snippet.edu

import java.util.Date
import scala.xml.{Text, XML, Unparsed}
import _root_.net.liftweb._
import http.{S, SHtml}
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

class ShowSlideSn {

  //for showslides - viewer
  def slideData() = {
    val infoError = "#slideHTML" #> <section class="slide" id="slide-1">
      <h1>Nie znaleziono slajdu</h1>
    </section>
    val id = S.param("id").openOr("0")
    if (id != "0") {
      Presentation.find(id) match {
        case Some(slide) => {
              "#title" #> <span>
                {slide.title.take(30)}
              </span> &
                "#slideHTML" #> Unparsed(slide.slides)

          }
        case _ => infoError
      }
    }
    else infoError
  }
}
