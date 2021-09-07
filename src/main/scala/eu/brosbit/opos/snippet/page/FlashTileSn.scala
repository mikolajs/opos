package eu.brosbit.opos.snippet.page

import net.liftweb.json.JsonDSL._
import eu.brosbit.opos.model.page.FlashTile

import scala.xml.Unparsed

trait FlashTileSn {
  def appendTile(url: String) = {
    val tiles = FlashTile.findAll("page" -> url).map(
      ft =>
        <div id="flashTile" style={"background-image: url('" +
          ft.img + "');"}
             onclick={"redirectToPage('" + ft.link + "');"}>
          <div class="trans-box">
            {Unparsed(ft.info)}
          </div>
        </div>)

    if (tiles.isEmpty) <span></span>
    else <div id="flashTilesContainer">
      <span id="closeFlash">
        <span class="glyphicon glyphicon-remove"></span>
      </span>
      {tiles}
    </div>
  }
}
