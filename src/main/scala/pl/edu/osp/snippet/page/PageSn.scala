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
import _root_.net.liftweb.http.{S, SHtml}
import Helpers._
import _root_.net.liftweb.json.JsonDSL._
import http.js.JsCmds.SetHtml


class PageSn {

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

  val dep = S.param("id").openOr("0")
  val pageDep = PageDepartment.find(dep).getOrElse(PageDepartment.create)
  val what = S.param("w").getOrElse("n") //all news by default

  //adding slide for department
  def slide = {
      "#slideImg [src]" #>  pageDep.img &
      "#slideInfo *" #> Unparsed(pageDep.info)
  }

  def buildMenu() = {
    val pathStart = "/page/" + dep + "?w="
    val addArticle = if(isTeacher) <a href={"/editarticle/0?d=" + dep}
                                      class="list-group-item">
      <span class="glyphicon glyphicon-plus"></span> Dodaj artykuł</a>
    else <span></span>
    "a" #> (List(addArticle, <a href={pathStart + "n"} class="list-group-item"> Aktualności </a>) :::
      (ArticleHead.findAll(("news" -> false)~("departmentId" -> dep), ("prior" -> 1)).map(
      art =>
        <a href={pathStart + art._id.toString} class="list-group-item">{art.title}</a>
      )))
  }


  def switchContent() = {
    what match {
      case "n" => {
        val newses = ArticleHead.findAll(("news" -> true)~("departmentId" -> dep), ("_id" -> -1))
        showNewses(newses)
      }
      case idArt:String => {
        ArticleHead.find(idArt) match {
          case Some(artH) => pageContent(artH)
          case _ => S.redirectTo("/page/" + dep)
        }
      }
      case _ => S.redirectTo("/page/" + dep)
    }
  }


  def showNewses(newses: List[ArticleHead]) = {
    val sizeP = 10
    val page = S.param("p").getOrElse("1")
    val pageInt = tryo(page.toInt).getOrElse(1)
    val pages = newses.size / sizeP + (if (newses.size % sizeP > 0) 1 else 0)

    val endNews = if (sizeP * pageInt > newses.size) newses.size else sizeP * pageInt
    val beginNews = if (endNews - sizeP < 0) 0 else endNews - sizeP

    val toShowNewses = newses.slice(beginNews, endNews)

    "#articleCont" #> "" &
      "#departmentInfo *" #> (if(newses.size > 0) newses.head.departmentName else "") &
      ".newsInfo" #> <div>
        {toShowNewses.map(news => createPinBox(news))}
      </div> &
      "li" #> (1 to pages).map(p => {
        <li><a  href={"/page/" + dep + "?w=n&p=" + p.toString}
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


  private def pageContent(articleHead: ArticleHead) = {

    val contentOption = ArticleContent.find(articleHead.content)

    "#newsCont" #> "" &
      "#depBody" #> <div id="pagecontent">
        <h1>
          {articleHead.title}
        </h1>
        <div id="pagebody">
          {Unparsed(contentOption.getOrElse(ArticleContent.create).content)}
        </div>
        <hr/>
        <p id="pageinfo">
          <span class="fullname">
            {articleHead.authorName}
          </span>
          <span class="date">
            {Formater.formatTime(articleHead._id.getDate())}
          </span>{if (isOwner(articleHead.authorId)) <span class="edit">
          <a href={"/editarticle/" + articleHead._id.toString} class="btn btn-info">
            <span class="glyphicon glyphicon-pencil"></span>Edytuj</a>
        </span>
        else <span></span>}
        </p>
      </div>
  }


}

