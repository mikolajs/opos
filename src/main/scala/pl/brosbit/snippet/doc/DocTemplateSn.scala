package pl.brosbit.snippet.doc

import scala.xml.Unparsed
import _root_.net.liftweb.util._
import _root_.pl.brosbit.model._
import _root_.net.liftweb.http.{S, SHtml}
import Helpers._
import _root_.net.liftweb.json.JsonDSL._


class DocTemplateSn extends BaseDoc {

  var id = S.param("id").getOrElse("0")
  var docHead = DocTemplate.find(id) match {
    case Some(templateHead) => templateHead
    case _ => {
      val docList = DocTemplate.findAll
      if (!docList.isEmpty) docList.head
      else DocTemplate.create
    }
  }

  def showHead() = {
    "span *" #> docHead.title &
      "#comment *" #> Unparsed(docHead.comment) &
    "#hiddenTemplate [value]" #> Unparsed(docHead.template)
  }

  def addEntry() = {
    var id = ""
    var cont = ""
    var nr = ""

    def save() {
      val docCont = DocContent.find(id).getOrElse(DocContent.create)
      docCont.template = docHead._id
      docCont.userId = user.id.get
      docCont.content = cont.trim
      docCont.userName = user.getFullName
      docCont.nr = tryo(nr.toInt).getOrElse(999)
      docCont.save

    }

    def delete() {
      DocContent.find(id) match {
        case Some(docCont) =>
          if(user.id.get == docCont.userId ) docCont.delete
        case _ =>
      }

    }
     "#idContent" #> SHtml.text(id, id = _) &
    "#content" #> SHtml.textarea(cont, cont = _) &
    "#nr" #> SHtml.text(nr, nr = _) &
      "#save" #> SHtml.submit("Zapisz", save) &
      "#delete" #> SHtml.submit("Usuń", delete)

  }

  def fullDocument() = {
    ".fulldocument *" #> DocContent.findAll(("template"->docHead._id.toString), ("nr" -> -1)).map(docItem => {
      <div>
        <h3><small>Dodane przez:</small> {docItem.userName}
          <span class="btn btn-default editbutton" id={docItem._id.toString} onclick="editDoc(this)">
          <span class="glyphicon glyphicon-pencil"  ></span>Edytuj</span></h3>
        <hr/><div class="docItem">{Unparsed(docItem.content)}</div>
      </div>
    })
  }

  def docTemplates() = {
    "li" #> DocTemplate.findAll.map(templateHead => {
      <li>
        <a href={"/documents/doctemplate/" + templateHead._id.toString}>
          {templateHead.title}
        </a>
      </li>
    })
  }

  def adminMenu() = {
    "ul" #> {
      if (isAdmin) {
        <ul>
          <li id="createTemplate">
            <a href="/documents/createtemplate/0">Utwórz nowy</a>
          </li>
          <li id="editTemplate">
            <a href={"/documents/createtemplate/" + docHead._id.toString}>Edytuj aktualny</a>
          </li>
          <li id="importTemplate">
            <a href={"/getdocument/" + docHead._id.toString}>Pobierz</a>
          </li>
        </ul>
      }
      else {
        <ul></ul>
      }
    }
  }

}
