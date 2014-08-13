/*
 * Copyright (C) 2011   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register - Wirtualny Dziennik)
 *   LICENCE: GNU AFFERO GENERAL PUBLIC LICENS Version 3 (AGPLv3)
 *   See: <http://www.gnu.org/licenses/>.
 */

package pl.brosbit.snippet.admin

import java.util.Date
import scala.xml.{ NodeSeq, Text, XML, Unparsed }
import _root_.net.liftweb.util._
import _root_.pl.brosbit.lib.MailConfig
import _root_.net.liftweb.common._
import _root_.pl.brosbit.model.page._
import _root_.pl.brosbit.model._
import _root_.net.liftweb.http.{ S, SHtml, RequestVar }
import _root_.net.liftweb.mapper.{ Ascending, OrderBy, By }
import _root_.net.liftweb.http.js._
import JsCmds._
import JE._
import Helpers._
import org.bson.types.ObjectId
import _root_.net.liftweb.json.JsonDSL._
import net.liftweb.mongodb.UpdateOption
import net.liftweb.mongodb.Upsert
import net.liftweb.mongodb.Multi

class AdminAddTagsSn {
  
  def addTag() = {
    var id = ""
    var name = ""

    def save() {
       NewsTag.find(id.trim) match {
        case Some(newsTag) => {
          newsTag.tag = name
          newsTag.save
        }
        case _ => {
          val newsTag = NewsTag.create
          newsTag.tag = name
          newsTag.save
        }
      }
       S.redirectTo("/admin/tags")
    }
    
    
    def delete(): Unit = {
        NewsTag.find(id.trim) match {
          case Some(newsTag) => newsTag.delete
          case _ => 
        }
        S.redirectTo("/admin/tags")
    }

    "#id" #> SHtml.text(id, x => id = x, "style" -> "display:none;", "id" -> "id") &
      "#name" #> SHtml.text(name, x => name = x.trim, "maxlength" -> "30", "id" -> "name") &
      "#save" #> SHtml.submit("Zapisz!", save, "onclick" -> "return validateForm()") &
      "#delete" #> SHtml.submit("Usuń!", delete, 
          "onclick" -> "return confirm('Na pewno chcesz usunąć tag?');")
  }
  
  def indexTags = {
      def mkIndex()   {
          var map:scala.collection.mutable.Map[String, Int] = scala.collection.mutable.Map()
          ArticleHead.findAll(("news"-> true)).map(artHead => {
              artHead.tags.foreach(tag => {
                  if(map.contains(tag)) map(tag) += 1
                  else map += (tag -> 1)
              	})
            })
            NewsTag.update(Nil,  ("$set"-> ("count" -> 0)), Multi)
            map.keys.foreach(key => {
                NewsTag.update(("tag"-> key),  ("$set"-> ("count" -> map(key))))
            })
      }
       "#save" #> SHtml.submit("Wykonaj ponowne indeksowanie!", mkIndex)
  }

  def tags  = {
    val newsTags = NewsTag.findAll
    "tr" #> newsTags.map(newsTag =>{
       <tr ondblclick={ "setData(this)" } id={newsTag._id.toString()} >
          <td>{newsTag.tag}</td>
        </tr>
    })
  }
  
  def tagsList = {
       val newsTags = NewsTag.findAll
    "tr" #> newsTags.map(newsTag =>{
       <tr >
          <td>{newsTag.tag + " (" + newsTag.count.toString +")"}</td>
        </tr>
    })
  }
  
}
