/*
 * Copyright (C) 2012   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register)
*    Apache License Version 2.0, January 2004  http://www.apache.org/licenses/
 */

package eu.brosbit.opos.snippet.page

import _root_.scala.xml.{NodeSeq, Unparsed, Null}
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.eu.brosbit.opos.model.page._
import _root_.eu.brosbit.opos.model._
import _root_.net.liftweb.http.{S, SHtml}
import Helpers._
import _root_.net.liftweb.json.JsonDSL._
import scala.xml.UnprefixedAttribute

class ArticleEditSn {

  val pageTypes = Map(true -> "Aktualności", false -> "Artykuły")
  val thumbDefault = "/images/nothumb.png"

  def editPage() = {

    var id = S.param("id").openOr("0")
    val dep = S.param("d").openOr("0")
    var title = ""
    var authorId = 0L
    var departId = ""
    var thumbnailLink = ""
    var introduction = ""
    var content = ""
    var prior = 9999
    var news = true
    var pageType = ""

    //wczytywanie danych wpisu
    ArticleHead.find(id) match {
      //stary wpis
      case Some(newsHead) => {
        val articleContent = ArticleContent.find(newsHead.content).getOrElse(ArticleContent.create)
        title = newsHead.title
        departId = newsHead.departmentId.toString
        authorId = newsHead.authorId
        thumbnailLink = newsHead.thumbnailLink
        introduction = newsHead.introduction
        content = articleContent.content
        news = newsHead.news
        prior = newsHead.prior
        pageType = pageTypes(news)
      }
      case _ => {
        departId = dep
        pageType = pageTypes(true)
        thumbnailLink = thumbDefault
      }
    }

    def save() {

      val newsHead = ArticleHead.find(id).getOrElse(ArticleHead.create)
      if (newsHead.authorId == 0L || isOwner(newsHead.authorId)) {
        newsHead.title = title
        news = if (pageType == pageTypes(true)) true else false
        newsHead.news = news
        newsHead.introduction = introduction
        newsHead.thumbnailLink = if (thumbnailLink.trim() == "") thumbDefault
          else thumbnailLink.trim

        newsHead.departmentId = new org.bson.types.ObjectId(departId)
        newsHead.prior = prior

        val user = User.currentUser.openOrThrowException("Brak użytkownika")
        val articleContent = ArticleContent.find(newsHead.content).getOrElse(ArticleContent.create)
        articleContent.content = content
        articleContent.save
        newsHead.content = articleContent._id
        if (newsHead.authorId == 0L) {
          newsHead.authorId = user.id.get
          newsHead.authorName = user.getFullName
        }
        newsHead.save
      }
      mkRedir(newsHead.news, departId, newsHead._id.toString)
    }

    def discard() {
      mkRedir(news, departId, id)
    }

    def delete() {
      {
        if (id.length > 11) if (isOwner(authorId)) deleteObjectById(id)
      }
      mkRedir(true, departId, "n")
    }

    val departList = PageDepartment.findAll.map(dep => (dep._id.toString, dep.name))
    val typePages = pageTypes.values.toList
    var nrRadios = 0;
    val choicePage = SHtml.radio(typePages, Full(pageType), pageType = _, "onclick" -> "editArticle.switchTagsDepart(this)").map(item => {
      nrRadios += 1
      var elem: scala.xml.Elem = <input></input>
      elem = elem % item.xhtml.head.attributes.append(new UnprefixedAttribute("id", ("radio" + nrRadios), Null))
      elem ++ <label for={"radio" + nrRadios}>
        {item.key.toString}
      </label> ++ <br/>
    })

    "#id" #> SHtml.text(id, id = _, "style" -> "display:none;") &
      "#title" #> SHtml.text(title, in => title = in.trim) &
      "#typePage" #> <div id="typePage">
        {choicePage}
      </div> &
      "#departs" #> SHtml.select(departList, Full(departId), departId = _) &
      "#prior" #> SHtml.number(prior, prior = _, 1, 9999) &
      "#thumbnail" #> SHtml.text(thumbnailLink, in => thumbnailLink = in.trim, "style" -> "display:none;") &
      "#introduction" #> SHtml.text(introduction, in => introduction = deleteBR(in.trim)) &
      "#editor" #> SHtml.textarea(content, in => content = in.trim) &
      "#save" #> SHtml.submit("Zapisz", save, "onclick" -> "return editArticle.beforeSubmit()") &
      "#delete" #> SHtml.submit("Usuń", delete, "onclick" -> "return confirm('Na pewno usunąć wpis?');") &
      "#discard" #> SHtml.submit("Porzuć", discard, "onclick" -> "return confirm('Na pewno porzucić bez zapisu?');")

  }


  private def deleteObjectById(id: String) {
    ArticleHead.find(id) match {
      case Some(articleHead) => {
        val articleContentOpt = ArticleContent.find(articleHead.content)
        if (!articleContentOpt.isEmpty) articleContentOpt.get.delete
        if (articleHead.news) this.updateNewsTags(Nil, articleHead.tags)
        articleHead.delete
      }
      case _ =>
    }
  }


  private def deleteBR(htmlText: String) = htmlText.replaceAll("<br>", " ").replaceAll("<br/>", " ")

  private def isOwner(idFromArticle: Long): Boolean = {
    User.currentUser match {
      case Full(user) => (idFromArticle == user.id.get || user.role.get == "a")
      case _ => false
    }
  }

  private def getFullLink(id: String) = "/index?w=w&id=" + id

  private def getToAddTagsByCompare(newTags: List[String], oldTags: List[String]): List[String] = {
    var toAddTags: List[String] = newTags
    for (tag <- oldTags) toAddTags = toAddTags.filter(_ != tag)
    toAddTags
  }

  private def getToDeleteTagsByCompare(newTags: List[String], oldTags: List[String]): List[String] = {
    var toDeleteTags: List[String] = oldTags
    for (tag <- newTags) toDeleteTags = toDeleteTags.filter(_ != tag)
    toDeleteTags
  }

  private def updateNewsTags(toAddTags: List[String], toDeleteTags: List[String]) {
    val increase = ("$inc" -> ("count" -> 1))
    val decrease = ("$inc" -> ("count" -> -1))
    toAddTags.foreach(tag => NewsTag.update(("tag" -> tag), increase))
    toDeleteTags.foreach(tag => NewsTag.update(("tag" -> tag), decrease))
  }

  private def createThumbnail(url: String) = {
    //do implementacji z wykorzystaniem FilesSn  i wykorzystania przy zapisie
    url
  }

  private def mkRedir(news: Boolean, dep: String, id: String) =
    S.redirectTo(
      if(news) {"/page/" + dep + "?w=n" }
      else {"/page/" + dep + "?w=" + id}
    )

}


