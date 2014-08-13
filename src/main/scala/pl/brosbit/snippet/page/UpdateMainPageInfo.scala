/*
 * Copyright (C) 2011   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register - Wirtualny Dziennik)
 *   LICENCE: GNU AFFERO GENERAL PUBLIC LICENS Version 3 (AGPLv3)
 *   See: <http://www.gnu.org/licenses/>.
 */
package pl.brosbit.snippet.page

import _root_.pl.brosbit.model.page._
import _root_.pl.brosbit.model._
import _root_.net.liftweb.json.JsonDSL._

trait UpdateMainPageInfo {
	def updateForumInfo(date:String, title:String, id:String){
	  val fullLink = "/forumpost/" + id
	  MainPageData.delete(("key" -> Keys.forum.toString) ~ ("link" -> fullLink))
	  val mainPageData = MainPageData.create
	  mainPageData.key = Keys.forum.toString
	  mainPageData.link = fullLink
	  mainPageData.title = title
	  mainPageData.save
	}
	
	def deleteMainPageInfo(id:String){
	   val fullLink = "/forumpost/" + id
	   MainPageData.delete(("key" -> Keys.forum.toString) ~ ("link" -> fullLink))
	}
}