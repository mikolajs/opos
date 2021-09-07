/*
 * Copyright (C) 2012   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register)
*    Apache License Version 2.0, January 2004  http://www.apache.org/licenses/
 */

package eu.brosbit.opos.snippet.page

import net.liftweb.util.Mailer.{PlainMailBodyType, To, Subject, From}

import scala.xml.{Unparsed}
import _root_.net.liftweb._
import util._
import common._
import _root_.eu.brosbit.opos._
import model.page._
import model._
import lib._
import _root_.net.liftweb.http.{S, SHtml}
import Helpers._
import _root_.net.liftweb.json.JsonDSL._


class MainSn extends FlashTileSn {

  val user = User.currentUser

  val contactMails = ContactMail.findAll.map(contactMail =>
    contactMail.description -> contactMail.description)

  val contactInfo = MapExtraData.getMapData("contactInfo")

  def sendMail() = {
    val contactMails = ContactMail.findAll
    val contactMailsToForm = contactMails.map(contactMail =>
      contactMail.description -> contactMail.description)
    var theme = ""
    var content = ""
    var mail = ""
    var selectedMail = ""

    def sendMail() {
      val emailList = contactMails.filter(contactMail => {
        contactMail.description == selectedMail
      })
      if (emailList.nonEmpty) {
        val emailToSend = emailList.head.mailAddress
        val body = content + "\n" + "----------\n Informacja wysłana ze strony przez: " + mail
        Mailer.sendMail(From("zestrony@zkpig26.gda.pl"), Subject(theme),
          To(emailToSend), PlainMailBodyType(body))
        S.redirectTo("/")
      }
      else S.notice("Błędny email")

    }

    "#theme" #> SHtml.text(theme, x => theme = x) &
      "#mailcontent" #> SHtml.textarea(content, x => content = x) &
      "#mail" #> SHtml.text(mail, x => mail = x) &
      "#select" #> SHtml.select(contactMailsToForm, Empty, x => selectedMail = x) &
      "#submit" #> SHtml.submit("Wyślij!", sendMail, "onclick" -> "return isValid();")

  }

  def mainInfo() = {

    "#nameCont *" #> Unparsed(if(contactInfo.contains("name"))
        addBRInsteadSemiColon(contactInfo("name")) else "") &
      "#patronCont *" #> (if(contactInfo.contains("patron")) contactInfo("patron") else "")  &
      "#streetCont *" #> (if(contactInfo.contains("street")) contactInfo("street") else "")  &
      "#cityCont *" #> (if(contactInfo.contains("city")) contactInfo("city") else "") &
      "#phoneCont *" #> (if(contactInfo.contains("phone")) contactInfo("phone") else "") &
      "#faxCont *" #> (if(contactInfo.contains("fax")) contactInfo("fax") else "") &
      "#mailCont *" #> (if(contactInfo.contains("email")) contactInfo("email") else "") &
      "iframe" #> (if(contactInfo.contains("maps")) <span>{Unparsed(contactInfo("maps"))}</span> else <span></span>)
  }

  private def addBRInsteadSemiColon(txt:String) = txt.split(';').mkString("<br/>")



  def news() = {
    val newses = ArticleHead.findAll(("news" -> true), ("_id" -> -1)).take(10)

      ".newsInfo" #> <div>
        {newses.map(news => createNewsBox(news))}
      </div>

  }

  def cards() = {
    "#flashTile" #> appendTile("/index")
  }

  private def createNewsBox(news: ArticleHead) = {
    <div class="row pine-box">
      <div class="col-md-3">
        <img  class="img-box featurette-image img-responsive" src={news.thumbnailLink} />
      </div>

      <div class="col-md-9 innerBox">
        <h2>
          {news.title}
        </h2>
        <div class="footPrint">
          <span class="glyphicon glyphicon-user"></span>
          <span class="fullname">
            {news.authorName}
          </span>
          <span class="glyphicon glyphicon-calendar"></span>
          <span class="date">
            {Formater.formatDate(news._id.getDate())}
          </span>
        </div>
        <div class="textBox">
          <div class="introNews">
            {Unparsed(news.introduction)}
          </div>
          <a href={"/page/" + news.departmentId } class="btn btn-small btn-info" >Czytaj całość </a>
        </div>
      </div>
      <div style="clear:both;"></div>
    </div>
  }

  def setScriptGoogleSearch() = {
    val code = ExtraData.getData("googlesearchcode")
    "script *" #> code
  }


  def imgLinks() = {
    "li" #> LinkTilesMainPage.findAll(Nil, "order" -> 1).map(
      lt =>
        <li><a href={lt.url} target="_blank"><img src={lt.img} /></a></li>
    )

  }

  ///========================== not used below =======================
/*
  //slider na głównej stronie - nie używam
  def slides = {
    val mpSlides = MainPageSlide.findAll
    var isFirst = true
    ".item" #> mpSlides.map(slide => {
      <div class={"item" + (if (isFirst) {
        isFirst = false; " active"
      } else "")}>
        <div class="fill" style={"background-image:url('" + slide.img + "');"}></div>
        <div class="carousel-caption">
          {Unparsed(slide.html)}
        </div>
      </div>
    }) &
    ".carousel-indicators" #> <ol class="carousel-indicators"> {
      for(n <- 0 until mpSlides.length)
        yield <li data-target="#carousel-main" data-slide-to={n.toString} class={if(n == 0) "active" else ""}></li>
      }
    </ol>
  }



  def submenuTopArticles() = {
    val allTag = <a href="/index/a">Wszystkie</a>
    val newsTags = allTag :: NewsTag.findAll.map(tag => <a href={"/index/t?tag=" + tag.tag}>
      {tag.tag +
        " (" + tag.count.toString() + ")"}
    </a>).toList
    val menuNews = List(("Aktualności", newsTags))


    val menuDepCont = PageDepartment.findAll(Nil, ("nr" -> 1)).map(pageDep => {
      <a href={"/index/b?d=" + pageDep._id.toString}>
        {pageDep.name}
      </a>
    }).toList
    val menuDep = List(("O Szkole", menuDepCont))
    val menu = menuNews ++ menuDep


      "#showSubmenu *" #> menu.map(m => {
        <a class="dropdown-toggle" data-toggle="dropdown" href="#" role="button" aria-haspopup="true" aria-expanded="false">
          {m._1} <span class="caret"></span>
        </a> ++
        <ul class="dropdown-menu">
        {m._2.map(ahref => <li>
          {ahref}
        </li>)}
        </ul>
      })
  }


  def switchContent() = {
    val what = S.param("w") openOr "a"
    what match {
      case "a" => {
        val newses = ArticleHead.findAll(("news" -> true), ("_id" -> -1))
        showNewses(newses, "")
      }
      case "t" => {
        val tag = S.param("tag").openOr("")
        if (tag == "") S.redirectTo("/index")
        else {
          val newsHeads = ArticleHead.findAll(("news" -> true) ~("tags", tag), ("_id" -> -1))
          showNewses(newsHeads, tag)
        }
      }
      case "b" => {
        val id = S.param("i").openOr("")
        val dep = S.param("d").openOr("")
        if (dep == "") S.redirectTo("/index")
        else {
          pageContent(dep, id)
        }
      }
      case _ => S.redirectTo("/index")
    }
  }


  def showNewses(newses: List[ArticleHead], tag: String) = {
    val sizeP = 10
    val page = S.param("p").getOrElse("1")
    val pageInt = tryo(page.toInt).getOrElse(1)
    val pages = newses.size / sizeP + (if (newses.size % sizeP > 0) 1 else 0)

    val endNews = if (sizeP * pageInt > newses.size) newses.size else sizeP * pageInt
    val beginNews = if (endNews - sizeP < 0) 0 else endNews - sizeP

    val toShowNewses = newses.slice(beginNews, endNews)
    val choiceContent = if (tag == "") "a?p=" else "t?tag=" + tag + "&p="
    "#articleCont" #> "" &
    "#departmentInfo *" #> tag &
    ".newsInfo" #> <div>
      {toShowNewses.map(news => createPinBox(news))}
    </div> &
      "li" #> (1 to pages).map(p => {
        <li><a  href={"/index/" + choiceContent + p.toString}
           class={ (if (p == pageInt) "actualPage" else "")}>
          {p.toString}
        </a></li>


      })

  }

  def showOneNews() = {
    def create(id: String) = {
      ArticleHead.find(id) match {
        case Some(newsHead) => {
          val contentOption = ArticleContent.find(newsHead.content)
          <div class="pagebody">
            {Unparsed(contentOption.getOrElse(ArticleContent.create).content)}
          </div> ++
            <div class="pageinfo">
              {if (isOwner(newsHead.authorId)) {
              <span class="edit">
                <a href={"/editarticle/" + newsHead._id.toString} class="btn btn-info">
                  <span class="glyphicon glyphicon-pencil"></span>
                  Edytuj</a>
              </span>
            }
            else <span></span>}<span class="btn btn-small btn-danger closeNewsButton" onclick="closeNews()">
              <span class="glyphicon glyphicon-remove"></span>
              Zamknij</span>
            </div>
        }
        case _ => <div>Błąd - brak wybranej lekcji</div>
      }
    }
    "#hiddenAjaxText" #> SHtml.ajaxText("", id => SetHtml("ajaxNews", create(id)))
  }



  private def isOwner(idFromArticle: Long): Boolean = {
    User.currentUser match {
      case Full(user) => (idFromArticle == user.id.get || user.role.get == "a")
      case _ => false
    }
  }

  private def createPinBox(news: ArticleHead) = {
    <div class="row pine-box">
      <div class="col-md-3">
        <img  class="img-box featurette-image img-responsive" src={news.thumbnailLink} />
      </div>

      <div class="col-md-9 innerBox">
        <h2>
          {news.title}
        </h2>
        <div class="footPrint">
          <span class="glyphicon glyphicon-user"></span>
          <span class="fullname">
            {news.authorName}
          </span>
          <span class="glyphicon glyphicon-calendar"></span>
          <span class="date">
            {Formater.formatDate(news._id.getDate())}
          </span>
        </div>
        <div class="textBox">
          <div class="introNews">
            {Unparsed(news.introduction)}
          </div>
          <span class="btn btn-small btn-info" onclick={"return showNews('" + news._id + "', this)"}>Czytaj dalej</span>
      </div>
    </div>
      <div style="clear:both;"></div>
    </div>
  }


  private def pageContent(dep: String, id: String) = {

    val pageHead = ArticleHead.find(id) match {
      case Some(page) => page
      case _ => {
        ArticleHead.findAll(("news" -> false)~("departmentId" -> dep), ("prior" -> -1) ) match {
          case ah::rest => ah
          case Nil => {
            val ah = ArticleHead.create
            ah.title = "Brak strony - błąd"
            ah
          }
        }
      }

    }

    val depName:String = PageDepartment.find(dep).getOrElse(PageDepartment.create).name

    val departmentArticles = ArticleHead.findAll(("news" -> false)~("departmentId" -> dep), ("prior" -> -1))

    val contentOption = ArticleContent.find(pageHead.content)

    "#newsCont" #> "" &
    "#depHeader *" #> depName &
    ".depMenu" #> departmentArticles.map(d =>
      <a href={"/index/b?d=" + dep + "&i=" + d._id.toString} class="list-group-item" >{d.title}</a>) &
      "#depBody" #> <div id="pagecontent">
        <h1>
          {pageHead.title}
        </h1>
        <div id="pagebody">
          {Unparsed(contentOption.getOrElse(ArticleContent.create).content)}
        </div>
        <hr/>
        <p id="pageinfo">
          <span class="fullname">
            {pageHead.authorName}
          </span>
          <span class="date">
            {Formater.formatTime(pageHead._id.getDate())}
          </span>{if (isOwner(pageHead.authorId)) <span class="edit">
          <a href={"/editarticle/" + pageHead._id.toString} class="btn btn-info">
            <span class="glyphicon glyphicon-pencil"></span>Edytuj</a>
        </span>
        else <span></span>}
        </p>
      </div>
  }
*/
}
