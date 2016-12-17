package pl.edu.osp.api

import net.liftweb._
import common._
import http._
import pl.edu.osp._
import java.io.ByteArrayInputStream
import model.page.ImageSlides
import _root_.net.liftweb.json.JsonDSL._
import pl.edu.osp.model.MapExtraData
import net.liftweb.util.Helpers._

object SlideImg {

  def create: Box[LiftResponse] = {
    val code = S.param("t").getOrElse("A")
    val imgs = ImageSlides.findAll(("code" -> code),("order" -> 1 ))
    val m = MapExtraData.getMapData(code)
    val r = if(m.isDefinedAt("r")) tryo(m("r").toInt).getOrElse(3600) else 3600
    val t = if(m.isDefinedAt("t")) tryo(m("t").toInt).getOrElse(10) else 10
    val str = "{ \"time\" : " + t + ", \"reload\" : " + r +  ", \"images\": [" +
      imgs.map(img => "\"" + img.src + "\"").mkString(",") +
    "]}"
    val inputStream = new ByteArrayInputStream(str.getBytes)
    if (inputStream.available() < 10) {
      Full(NotFoundResponse("Not found"))
    }
    else {
      Full(StreamingResponse(inputStream, () =>
        (), inputStream.available().toLong, ("Content-Type", "text/json") :: Nil, Nil, 200))
    }
  }

}