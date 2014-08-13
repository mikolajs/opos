
//     object nrPhoto extends MappedString(this,3) //nr galerii (własny)
//     object id_gallery extends MappedString(this,120) //id galerii
//     object nrOfPhotos extends MappedString(this, 3) //ilość zdjęć
//     object thumb extends MappedText(this)
//     object title extends MappedString(this, 150)
//     object urls extends MappedText(this)


import java.io.File
import java.net.URL
import javax.mail._

import com.google.gdata.client._
import com.google.gdata.client.photos._
import com.google.gdata.data._
import com.google.gdata.data.media._
import com.google.gdata.data.photos._

import scala.collection.JavaConversions._

class Gallery {
 //var nrPhoto = ""
 //var id_gallery = ""
 var nrPhotos = ""
 var description = ""
 var thumb = ""
 var title = ""
 var urls = ""
}

val apiUrlStr = "https://picasaweb.google.com/data/feed/api/user/"

val myService:PicasawebService = new PicasawebService("virtual-register")
myService.setUserCredentials("zkpig26gdansk@gmail.com", "lgOS1971as")
val feedUrl:URL = new URL("https://picasaweb.google.com/data/feed/api/user/zkpig26gdansk?kind=album")

val myUserFeed:UserFeed  = myService.getFeed(feedUrl, classOf[UserFeed] );
val albumEntries = asJavaList(myUserFeed.getAlbumEntries())
var galList:List[Gallery] = Nil

for ( myAlbum <- albumEntries) {
    val g = new Gallery()
    g.title = myAlbum.getTitle().getPlainText()
    g.description = myAlbum.getDescription().getPlainText()

//     println(myAlbum.getTitle().getPlainText()) //tytuł 
//     println(myAlbum.getDescription().getPlainText()) //opis?
    val feedUrl2:URL = new URL("https://picasaweb.google.com/data/feed/api/user/zkpig26gdansk/albumid/" + myAlbum.getId.toString.split("/").last )
    val feed:AlbumFeed= myService.getFeed(feedUrl2, classOf[AlbumFeed])
    var urls:List[(String,String)] = Nil
    var nrPhoto = 0
    for(photo <- feed.getPhotoEntries()) {
      nrPhoto += 1
      urls = (("http" + photo.getMediaThumbnails().get(1).getUrl().substring(5)) -> ("http" + photo.getMediaContents().get(0).getUrl().substring(5)))::urls
//       println(photo.getMediaThumbnails().get(1).getUrl())
//       println(photo.getMediaThumbnails().get(2).getUrl())
//       println(photo.getMediaContents().get(0).getUrl()) //full size
    }
  g.nrPhotos = nrPhoto.toString
  g.thumb = urls.last._1
  g.urls = "[" + urls.map(x => "['" + x._1 + "'," + " '" + x._2 + "']").mkString + "]" 
  galList = g::galList
}

galList.foreach(g => { println(g.nrPhotos);println(g.title);println(g.thumb);println(g.urls) })



