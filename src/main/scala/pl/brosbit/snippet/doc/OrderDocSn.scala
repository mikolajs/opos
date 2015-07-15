package pl.brosbit.snippet.doc

import _root_.net.liftweb.util._
import _root_.pl.brosbit.model._
import _root_.net.liftweb.http.{S, SHtml}
import Helpers._
import _root_.net.liftweb.json.JsonDSL._
import scala.xml.Unparsed


class OrderDocSn extends BaseDoc {

  var id = S.param("id").getOrElse("0")
  var inorder = ""
  val docTempl = DocTemplate.find(id).getOrElse(DocTemplate.create)
  val docs = DocContent.findAll("template" -> docTempl._id.toString, "nr" -> 1)


  def mkOrder() = {

    def save() {
      val order = inorder.split(',')
      var nr = 1;
      sortDocById(order).map(d => {
        d.nr = nr;
        d.save
        nr += 1
    })

      S.redirectTo("/documents/doctemplate/" + id)
    }

    def discard() { S.redirectTo("/documents/doctemplate/" + id) }

      "#title *" #> docTempl.title &
      "#descript *" #> Unparsed(docTempl.comment) &
      "#inorder" #> SHtml.text(inorder, inorder = _) &
      "#save" #> SHtml.submit("Zapisz", save) &
      "#cancel" #> SHtml.submit("Porzuć", discard) &
      "#docs li *" #> docs.map(d => {
         (<h4 id={d._id.toString}><small>autor:</small> {d.userName}</h4> ++
             <hr/> ++ <div class="docItem">{Unparsed(d.content)}</div>)
       })
  }

  private def sortDocById(order:Array[String]) =
    docs.sortWith((f, l ) => {
      order.indexOf(f._id.toString) < order.indexOf(l._id.toString)
    })


}