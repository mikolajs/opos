package eu.brosbit.opos.snippet.admin

import com.mongodb.gridfs.GridFS
import net.liftweb.mongodb._
import net.liftweb.util.Helpers._

import net.liftweb.http.{ S}
import org.bson.types.ObjectId

class AdminGC {
  val del = S.param("del").getOrElse("0")
  val page = S.param("p").getOrElse("1").toInt

  if(del != "0") delete()

  def allImages() = {
    val perPage = 30
    import eu.brosbit.opos.lib.GCFiles
    val GC = new GCFiles()
    val images = GC.getAllInGridFS()
    val imgPage = images.drop(perPage * (page - 1)).take(perPage)
    ".row *" #> imgPage.map(img => "img" #> <img src={"/img/" + img} class="myImg"/> &
      "a [href]" #> ("/admin/gc?del=" + img.toString)
    ) &
      "li" #> (1 to (images.length / perPage + 1)).map(p => {
        <li>
          <a href={"/admin/gc?p="+ p.toString}
             class={(if (p == page) "actualPage" else "")}>
            {p.toString}
          </a>
        </li>
      })
  }



  def delete() = MongoDB.use(DefaultMongoIdentifier) { db =>
      val fs = new GridFS(db)
      fs.remove(new ObjectId(del))

  }




}
