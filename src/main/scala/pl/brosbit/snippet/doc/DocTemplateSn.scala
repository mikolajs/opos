package pl.brosbit.snippet.doc

import scala.xml.Unparsed
import _root_.net.liftweb.util._
import _root_.pl.brosbit.model._
import _root_.net.liftweb.http.{S, SHtml}
import Helpers._
import _root_.net.liftweb.json.JsonDSL._
import _root_.net.liftweb.json.JsonAST._


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
      val nrInt =  tryo(nr.toInt).getOrElse(999)
      val q2 = ("nr" -> ("$gt" -> (nrInt - 1)))
      val q1 = ("template" -> docHead._id.toString)
      if(docCont.nr == 0)
        DocContent.update(q1 ~ q2  , ("$inc"-> ("nr" -> 1)))
      docCont.nr = nrInt
      docCont.save

    }

    def delete() {
      DocContent.find(id) match {
        case Some(docCont) =>
          if(user.id.get == docCont.userId || isAdmin ) docCont.delete
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
    ".fulldocumentCont *" #> DocContent.findAll(("template"->docHead._id.toString), ("nr" -> 1)).map(docItem => {
      <div class="fulldocument">
        <h3><small>Dodane przez:</small> {docItem.userName}
          <span class="btn btn-default editbutton" name={"#" + docItem.nr.toString} id={docItem._id.toString} onclick="editDoc(this)">
          <span class="glyphicon glyphicon-pencil"  ></span> Edytuj</span></h3>
        <hr/><div class="docItem">{Unparsed(docItem.content)}</div>
      </div> ++ <span class="btn btn-default addbutton"  onclick={"addDoc(" + docItem.nr.toString + ")"}>
        <span class="glyphicon glyphicon-plus"  ></span> Dodaj</span>
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
          <li id="editTemplate">
            <a href={"/documents/orderdoc/" + docHead._id.toString}>Uporządkuj aktualny</a>
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
