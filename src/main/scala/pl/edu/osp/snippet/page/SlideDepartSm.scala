package pl.edu.osp.snippet.page

import _root_.net.liftweb.util._
import net.liftweb.json.JsonDSL._
import pl.edu.osp.model.page.{ArticleHead, ArticleContent}
import net.liftweb.http.S
import Helpers._
import scala.xml.Unparsed

object SlideDepartSn {


  def data() = {
    val deps = S.param("d").getOrElse("").split(',').toList
    val ahs = if(deps.isEmpty && deps.head == "all")
            ArticleHead.findAll("news" -> true)
          else
            ArticleHead.findAll(("news" -> true) ~ ("tags" -> ("$in" -> deps)))
    val acs = ArticleContent.findAll("_id" -> ("$in" -> ahs.map(_.content.toString)))
    //println("============ article content size:  " + acs.length.toString )
    var nr = 0
    "#slideHTML" #> ahs.map(ah => {
      val ac = acs.find(a => a._id.toString == ah.content.toString).getOrElse(ArticleContent.create)
      nr += 1
    <section id={"slide_" + nr.toString} class="slide">
      <h1>{ah.title}</h1>
    <div>
      {Unparsed(ac.content)}
    </div>
    </section>
    }

    )
  }

}
