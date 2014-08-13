/* Copyright (C) 2011   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register - Wirtualny Dziennik)
 *   LICENCE: GNU AFFERO GENERAL PUBLIC LICENS Version 3 (AGPLv3)
 *   See: <http://www.gnu.org/licenses/>.
 */

package pl.brosbit.snippet.page

import _root_.scala.xml.{ NodeSeq, Text }
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.pl.brosbit.model.page._
import pl.brosbit.model._
import _root_.net.liftweb.http.{ S }
import Helpers._

class GallerySn {

  //dodaje listę galeriigalleryLe
  def drawSlider() = {
    val galleries = Gallery.findAll
    "li" #> galleries.map(gallery => {
      <li><img src={ gallery.photos.head.thumbnail } width="144" height="108" 
      alt={ gallery.title } onclick={ "window.location='/gallery/" + gallery._id.toString  + "'"}/></li>
    })
  }

  //dodaje dwie zmienne
  def gallery() = {

    val id = S.param("id").openOr("0")
    Gallery.find(id)
    val gallery = if (id == 0) {
      Gallery.findAll match {
        case Nil => Gallery.create
        case galleries => galleries.head
      }
    } else {
      Gallery.find(id) match {
        case Some(gallery) => gallery
        case _ => {
          Gallery.findAll match {
            case Nil => Gallery.create
            case galleries => galleries.head
          }
        }
      }
    }
    var counter = 0
    "#gallerytitle" #> <span>{ gallery.title }</span> &
      ".grid_3" #> gallery.photos.map(photo => {
        val extraClass = if (counter % 5 == 0) "alpha" else if (counter % 4 == 0) "omega" else ""
        counter += 1
          <a href={ photo.full } rel="group1">
            <img src={ photo.thumbnail } alt="" class="frame"/>
          </a>
      })
  }
}
