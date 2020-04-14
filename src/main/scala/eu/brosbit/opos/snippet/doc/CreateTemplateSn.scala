package eu.brosbit.opos.snippet.doc

import _root_.net.liftweb.util._
import _root_.eu.brosbit.opos.model._
import _root_.net.liftweb.http.{S, SHtml}
import Helpers._
import _root_.net.liftweb.json.JsonDSL._
import net.liftweb.common.Full




class CreateTemplateSn extends BaseDoc {

  var id = S.param("id").getOrElse("0")
  var title = ""
  var comment = ""
  var table = false
  var template = ""
  DocTemplate.find(id) match {
    case Some(templateHead) => {
      title = templateHead.title
      comment = templateHead.comment
      template = templateHead.template
      table = templateHead.tab
    }
    case _ =>
  }


  def edit() = {

    def save() {
      val docHead = DocTemplate.find(id) match {
        case Some(templateHead) => templateHead
        case _ => DocTemplate.create
      }
      docHead.title = title.trim
      docHead.tab = table
      docHead.comment = comment.trim
      docHead.template = template.trim
      docHead.save
      S.redirectTo("/documents/doctemplate/" + id)
    }

    def delete() {
      if (id.length > 20) {
        DocTemplate.find(id) match {
          case Some(templateHead) =>
            DocContent.delete(("template" -> templateHead._id.toString))
          case _ =>
        }
      }
      S.redirectTo("/documents/doctemplate")
    }

    def discard() {
      S.redirectTo("/documents/doctemplate")
    }

    "#id" #> SHtml.text(id, id = _) &
      "#title" #> SHtml.text(title, title = _) &
      "#table" #> SHtml.checkbox_id(table, (x: Boolean) => table = x, Full("table")) &
      "#comment" #> SHtml.textarea(comment, comment = _) &
      "#template" #> SHtml.textarea(template, template = _) &
      "#save" #> SHtml.submit("Zapisz", save) &
      "#delete" #> SHtml.submit("Usuń", delete) &
      "#discard" #> SHtml.submit("Porzuć", discard)
  }


}
