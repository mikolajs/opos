/*
 * Copyright (C) 2012   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register)
*    Apache License Version 2.0, January 2004  http://www.apache.org/licenses/
 */

package pl.edu.osp.snippet.page

import scala.xml.{Unparsed}
import _root_.net.liftweb._
import util._
import common._
import _root_.pl.edu.osp._
import model.page._
import model._
import lib._
import _root_.net.liftweb.mapper.By
import _root_.net.liftweb.http.{S, SHtml}
import Helpers._
import _root_.net.liftweb.json.JsonDSL._
import http.js.JsCmds.SetHtml


class MainSn {

  val user = User.currentUser

  val isTeacher = if (user.isEmpty) false
  else {
    user.openOrThrowException("Niemożliwe").role.get match {
      case "a" => true
      case "n" => true
      case "d" => true
      case _ => false
    }
  }

  //slider na głównej stronie
  def slides = {
    val mpSlides = MainPageSlide.findAll
    var isFirst = true
    ".item" #> mpSlides.map(slide => {
      <div class={"item" + (if (isFirst) {
        isFirst = false; " active"
      } else "")}>
        <img src={slide.img} alt={slide.desc}/>

        <div class="carousel-caption">
          {Unparsed(slide.html)}
        </div>
      </div>
    })
  }

  def links = {
    val mainPageLinks = MainPageLinks.findAll match {
      case head :: list => head
      case _ => MainPageLinks.create
    }
    ".grid_3" #> mainPageLinks.links.map(linkGroup => {
      <div class="grid_3">
        <h3>
          {linkGroup.name}
        </h3>
        <ul>
          {linkGroup.links.map(link => <li>
          <a href={link.url}>
            {link.title}
          </a>
        </li>)}
        </ul>
      </div>
    })
  }



  def submenuTopArticles() = {
    val allTag = <li><a href="/index/a">Wszystkie</a></li>
    val newsTags = allTag :: NewsTag.findAll.map(tag => <li><a href={"/index/t?tag=" + tag.tag}>
      {tag.tag +
        " (" + tag.count.toString() + ")"}
    </a></li>).toList
    val menuNews = List(("Aktualności", newsTags))


    val menuDepCont = PageDepartment.findAll(Nil, ("nr" -> 1)).map(pageDep => {
      <li><a href={"/index/b?d=" + pageDep._id.toString}>
        {pageDep.name}
      </a></li>
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
      }) &
     "#addNews *" #>  (if(isTeacher) <a href="/editarticle/0" >Dodaj artykuł/stronę</a> else <span></span>)
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
    val sizeP = 20
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
            else <span></span>}<span class="closeNewsButton" onclick="closeNews()">
              <img src="/images/delico.png"/>
              Zamknij</span>
            </div>
        }
        case _ => <div>Błąd - brak wybranej lekcji</div>
      }
    }
    "#hiddenAjaxText" #> SHtml.ajaxText("", id => SetHtml("ajaxNews", create(id)))
  }

  def logIn() = {
    var email = ""
    var pass = ""
    var pesel = ""

    def mkLog() = {
      if (user.isEmpty) {
        User.findAll(By(User.email, email.trim)) match {
          case u :: other => {
            User.logUserIn(u)
          }
          case _ =>
        }
      }


    }
    user match {
      case Full(u) => {
        "form" #> <a title="WYLOGUJ!" href="/user_mgt/logout">
          <button type="button" class="btn btn-info">
            <span class="glyphicon glyphicon-log-out"></span>{u.getFullName}
          </button>
        </a>
      }
      case _ => {
        "#email" #> SHtml.text(email, email = _) &
          "#password" #> SHtml.password(pass, pass = _) &
          "#pesel" #> SHtml.text(pesel, pesel = _) &
          "#mkLog" #> SHtml.submit("Zaloguj", mkLog)
      }
    }

  }

  def setScriptGoogleSearch() = {
    val code = ExtraData.getData("googlesearchcode")
    "script *" #> code
  }


  private def isOwner(idFromArticle: Long): Boolean = {
    User.currentUser match {
      case Full(user) => (idFromArticle == user.id.get || user.role.get == "a")
      case _ => false
    }
  }

  private def createPinBox(news: ArticleHead) = {
    <div class="pine-box">
      <div class="imgBox">
        <img src={news.thumbnailLink}/>
      </div>

      <div class="innerBox">
        <h2 onclick={"showNewsTitle('" + news._id + "', this)"}>
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
          <span class="readMore" onclick={"return showNews('" + news._id + "', this)"}>Czytaj dalej</span>
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


}
