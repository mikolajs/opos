package pl.edu.osp.api

import net.liftweb._
import common._
import http._
import pl.edu.osp._
import java.io.ByteArrayInputStream
import model.page.ImageSlides
import _root_.net.liftweb.json.JsonDSL._

object SlideImg {

  def create: Box[LiftResponse] = {
    val imgs = ImageSlides.findAll(("code" -> "A"),("order" -> 1 ))
    val str = "{\"images\": [" +
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