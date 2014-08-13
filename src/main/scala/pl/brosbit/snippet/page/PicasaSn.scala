/* Copyright (C) 2011   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register - Wirtualny Dziennik)
 *   LICENCE: GNU AFFERO GENERAL PUBLIC LICENS Version 3 (AGPLv3)
 *   See: <http://www.gnu.org/licenses/>.
 */

package pl.brosbit.snippet.page

import _root_.scala.xml.{ NodeSeq }
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.pl.brosbit.model.page._
import _root_.pl.brosbit.model._
import _root_.net.liftweb.mapper.{ By }
import _root_.net.liftweb.http.{ SHtml }
import Helpers._
import _root_.pl.brosbit.lib.{ PicasaIndex }
import _root_.net.liftweb.json.JsonDSL._

class PicasaSn {
  def make(): Boolean = {
    val picasa = new PicasaIndex()
    if (picasa.make) true
    else false
  }

  def forms() = {
    val addressExtraData = ExtraData.findAll(("key" -> "picasamail")) match {
      case Nil => ExtraData.create
      case list => list.head
    }
    val passwordExtraData = ExtraData.findAll(("key" -> "picasapass")) match {
      case Nil => ExtraData.create
      case list => list.head
    }
    var password = passwordExtraData.data
    var address = addressExtraData.data
    def save() {
      if (password.length > 5 && address.length > 5) {
        addressExtraData.key = "picasamail"
        addressExtraData.data = address
        addressExtraData.save
        passwordExtraData.key = "picasapass"
        passwordExtraData.data = password
        passwordExtraData.save
      }
    }

    "#adres" #> SHtml.text(address, x => address = x.trim) &
      "#pass" #> SHtml.text(password, x => password = x.trim, "type" -> "password") &
      "#submit" #> SHtml.submit("Zapisz!", save) &
      "#index" #> SHtml.submit("Indeksuj!", make)
  }
}

