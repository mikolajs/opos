/* Copyright (C) 2011   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register - Wirtualny Dziennik)
 *   LICENCE: GNU AFFERO GENERAL PUBLIC LICENS Version 3 (AGPLv3)
 *   See: <http://www.gnu.org/licenses/>.
 */

package pl.brosbit.lib

import _root_.pl.brosbit.model.page._
import pl.brosbit.model._
import java.net.URL
import javax.mail._

import com.google.gdata.client._
import com.google.gdata.client.photos._
import com.google.gdata.data._
import com.google.gdata.data.media._
import com.google.gdata.data.photos._

import scala.collection.JavaConversions._

class PicasaIndex {

  def make(): Boolean = {
    val email = ExtraData.getData("picasamail") + "@gmail.com"
    val password = ExtraData.getData("picasapass")
    if (email.split("@")(0).trim.length == 0 || password.trim == 0) return false

    val apiUrlStr = "https://picasaweb.google.com/data/feed/api/user/"

    val myService: PicasawebService = new PicasawebService("virtual-register")

    myService.setUserCredentials(email, password)
    val feedUrl: URL = new URL("https://picasaweb.google.com/data/feed/api/user/" + email + "?kind=album")

    val myUserFeed: UserFeed = myService.getFeed(feedUrl, classOf[UserFeed]);
    val albumEntries = bufferAsJavaList(myUserFeed.getAlbumEntries())
    var galList: List[Gallery] = Nil

    Gallery.drop
    for (myAlbum <- albumEntries) {
      val gallery = Gallery.create
      gallery.title = myAlbum.getTitle().getPlainText
      gallery.description = myAlbum.getDescription().getPlainText()

      val feedUrl2: URL = new URL("https://picasaweb.google.com/data/feed/api/user/" + email + "/albumid/" + myAlbum.getId.toString.split("/").last)
      val feed: AlbumFeed = myService.getFeed(feedUrl2, classOf[AlbumFeed])
      var photos: List[Photo] = Nil

      for (photoEntry <- feed.getPhotoEntries()) {
        val photo = Photo("http" + photoEntry.getMediaThumbnails().get(1).getUrl().substring(5),
          "http" + photoEntry.getMediaContents().get(0).getUrl().substring(5))
        photos = photo :: photos
      }
      gallery.photos = photos
      gallery.save
    }
    return true
  }

}

