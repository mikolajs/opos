/*
 * Copyright (C) 2011   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   LICENCE: GNU AFFERO GENERAL PUBLIC LICENS Version 3 (AGPLv3)
 *   See: <http://www.gnu.org/licenses/>.
 */

package pl.brosbit.snippet.page

import _root_.scala.xml.{ NodeSeq, Unparsed}
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.pl.brosbit.model.page._
import _root_.pl.brosbit.model._
import _root_.net.liftweb.mapper.{ Descending, OrderBy, By }
import _root_.net.liftweb.http.{ S, SHtml}
import Helpers._
import _root_.pl.brosbit.lib.{ Formater }
import java.util.Date
import _root_.net.liftweb.json.JsonDSL._

class ForumSn extends UsersOperations with ForumBaseMenu {
  
  //zawartość strony z listą wątków w wybranym dziale
  def showThreadsTable() = {
    val threads = getThreads

    "#tagsInfo *" #> S.param("tags").openOr(" brak ") &
    ".tagsItem" #> allTags &
      "#tbodytr" #> threads.map(thread => {
        <tr>
          <td><a href={ "/forumpost/" + thread._id.toString }>{ thread.title }</a>
          <br/> <span class="fullname">{ thread.authorName }</span>
          <span class="date">{ Formater.formatTime(new Date(thread._id.getTime())) }</span></td>
          <td>{ thread.count.toString }</td>
          <td>{ Unparsed(thread.lastInfo) }</td>
        </tr>
      })
  }
  
  def linkAddNewThread() = {
    "#linkaddnewthread" #> (if(isLoged) 
    <a href={"/editthread"}> Dodaj nowy wątek </a>
    else <a href="/user_mgt/login">Zaloguj się aby móc dodawać wątki</a>)
  }

  
  private def getThreads() = {
      val tagsString = S.param("tags").openOr("")
      if(tagsString == "") ForumThreadHead.findAll
      else {
          val queryList = tagsString.split(",").toList
          ForumThreadHead.findAll("tags"-> ("$in" -> queryList))
      }
     
  }

}

