package pl.brosbit.snippet.admin

import scala.xml.{NodeSeq, Text, XML, Unparsed}
import _root_.net.liftweb.util._
import pl.brosbit.model.MapExtraData

import _root_.net.liftweb.http.{S, SHtml}
import  _root_.net.liftweb.util.Helpers._

class AdminContactSn {
  val key = "contactInfo"
   val infoData = MapExtraData.getMapData(key)
  def data() = {
    var name = ""
    var patron = ""
    var street = ""
    var city = ""
    var phone = ""
    var fax = ""
    var email = ""
    var maps = ""

    if(!infoData.isEmpty) {
      name = if(infoData.contains("name")) infoData("name") else ""
      patron = if(infoData.contains("patron")) infoData("patron") else ""
      street = if(infoData.contains("street")) infoData("street") else ""
      city = if(infoData.contains("city")) infoData("city") else ""
      phone = if(infoData.contains("phone")) infoData("phone") else ""
      fax = if(infoData.contains("fax")) infoData("fax") else ""
      email = if(infoData.contains("email")) infoData("email") else ""
      maps = if(infoData.contains("maps")) infoData("maps") else ""
    }

    def save() = {
      val infoDataM = scala.collection.mutable.Map[String, String]()
      infoDataM("name") = name
      infoDataM("patron") = patron
      infoDataM("street") = street
      infoDataM("city") = city
      infoDataM("phone") = phone
      infoDataM("fax") = fax
      infoDataM("email") = email
      infoDataM("maps") = maps
      MapExtraData.setMapData(key, infoDataM.toMap)
    }

    "#name" #>  SHtml.text(name, name = _) &
    "#patron" #> SHtml.text(patron, patron = _) &
    "#street" #> SHtml.text(street, street = _) &
    "#city" #> SHtml.text(city, city = _) &
    "#phone" #> SHtml.text(phone, phone = _) &
    "#fax" #> SHtml.text(fax, fax = _) &
    "#email" #> SHtml.text(email, email = _) &
    "#maps" #> SHtml.text(maps, maps = _) &
    "#save" #> SHtml.submit("Zapisz", save)
  }
}
