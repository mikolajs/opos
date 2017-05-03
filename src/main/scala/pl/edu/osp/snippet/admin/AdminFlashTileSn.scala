package pl.edu.osp.snippet.admin

import scala.xml.{NodeSeq, Unparsed}
import _root_.net.liftweb.util._

import _root_.pl.edu.osp.model.page._
import _root_.net.liftweb.http.{S, SHtml}
import Helpers._

class AdminFlashTileSn {
  def addTile() = {
    var id = ""
    var page = ""
    var link = ""
    var img = ""
    var info =""

    def addTile() {
      val flashTile = FlashTile.find(id.trim) match {
        case Some(ft) => ft
        case _ => FlashTile.create
      }
      flashTile.link = link.trim
      flashTile.page = page.trim
      flashTile.img = img.trim
      flashTile.info = info.trim
      flashTile.save
      S.redirectTo("/admin/flashtile")
    }


    def delTile(): Unit = {
      FlashTile.find(id.trim) match {
        case Some(flashTile) => flashTile.delete
        case _ =>
      }
      S.redirectTo("/admin/flashtiles")
    }

    "#id" #> SHtml.text(id, x => id = x, "style" -> "display:none;", "id" -> "id") &
      "#page" #> SHtml.text(page, page = _) &
      "#link" #> SHtml.text(link, link = _) &
      "#info" #> SHtml.textarea(info, info = _) &
      "#img" #> SHtml.text(img, img = _ ) &
      "#save" #> SHtml.submit("Zapisz!", addTile, "onclick" -> "return validateForm()") &
      "#delete" #> SHtml.submit("Usuń!", delTile,
        "onclick" -> "return confirm('Na pewno chcesz usunąć ten kafelek flash?');")
  }

  def tiles(n: NodeSeq): NodeSeq = {
    val tiles = FlashTile.findAll
    val node: NodeSeq = <tbody>
      {for (tile <- tiles) yield {
        <tr ondblclick={"setData(this)"} title={"ID: " + tile._id.toString}
            id={tile._id.toString()}>
          <td> {tile.page.toString}</td>
          <td>{tile.link}</td>
          <td><img src={tile.img} style="width:200px;" /></td>
          <td>{Unparsed(tile.info)}</td>
        </tr>
      }}
    </tbody>
    node
  }
}
