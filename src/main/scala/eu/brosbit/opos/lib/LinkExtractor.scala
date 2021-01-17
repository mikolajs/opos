package eu.brosbit.opos.lib

import scala.util.matching.Regex

object LinkExtractor {

  ///TODO: Implement this methods
  def findFilesIds(data:String):List[String] = {
    val reg = new Regex("/file/([0-9]{24})\\.(.[0-9,a-z,A-Z]{1:6})")
    Nil
  }
  def findImagesIds(data:String):List[String] = Nil

}
