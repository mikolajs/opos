package pl.edu.osp.snippet.admin

import _root_.net.liftweb.http.{S, SHtml}
import _root_.net.liftweb.json.JsonDSL._
import _root_.pl.edu.osp.model.page._
import net.liftweb.util.Helpers._

import scala.xml.XML

class AdminLinkTilesSn {

  def addLinks() = {
    var xmlDataStr = ""
    def add() {

      println(xmlDataStr)
      val xmlData = XML.loadString(xmlDataStr)
      var linksList: List[(String, String)] = Nil
      (xmlData \ "link").foreach(link => {
        val img = (link \ "@img").toString
        val url = (link \ "@url").toString
        linksList = (img, url) :: linksList
      })
      LinkTilesMainPage.drop
      var i = 0
      linksList = linksList.reverse
      linksList.foreach(
        ll => {
          val ltmp = LinkTilesMainPage.create
          ltmp.img = ll._1
          ltmp.url = ll._2
          ltmp.order = i
          i += 1
          ltmp.save
        })
      S.redirectTo("/admin/linktiles")
    }
  "#xmlData" #> SHtml.text(xmlDataStr, xmlDataStr = _, "style" -> "display:none;") &
    "#submit" #> SHtml.submit("ZAPISZ", add, "onclick" -> "return saveAll()")
}

  def showLinks() = {
    val tiles = LinkTilesMainPage.findAll(Nil, "order" -> 1)

    ".linkTr" #> tiles.map(t => {
      ".imgLink" #> <td>
        <img src={t.img} />
      </td> &
        ".urlLink" #> <td>
          {t.url}
        </td>
    })
  }

}

