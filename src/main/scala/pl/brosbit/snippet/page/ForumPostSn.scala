/*
 * Copyright (C) 2011   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register - Wirtualny Dziennik)
 *   LICENCE: GNU AFFERO GENERAL PUBLIC LICENS Version 3 (AGPLv3)
 *   See: <http://www.gnu.org/licenses/>.
 */

package pl.brosbit.snippet.page

import scala.xml.{ NodeSeq, Unparsed }
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.pl.brosbit.model.page._
import _root_.pl.brosbit.model._
import _root_.net.liftweb.http.{ S, SHtml, RequestVar }
import Helpers._
import java.util.Date
import _root_.pl.brosbit.lib.Formater
import _root_.net.liftweb.mongodb._
import org.bson.types.ObjectId
import _root_.net.liftweb.json.JsonDSL._
import page.ForumThreadHead

class ForumPostSn extends UsersOperations with UpdateMainPageInfo with ForumBaseMenu {

	val id = S.param("id").getOrElse("")
	val threadHead:ForumThreadHead = 
	  ForumThreadHead.find(id) match {
	  case Some(forumThreadHead) => forumThreadHead
	  case _ => S.redirectTo("/forum");ForumThreadHead.create
	}
	val threadContent = ForumThreadContent.find(threadHead.content).getOrElse(ForumThreadContent.create)
	if(threadContent._id != threadHead.content) threadHead.content = threadContent._id

  

  def showThread() = {
    
    val editNode = if(isPostOwner(threadHead.authorId)) <a href={"/editthread?id=" + threadHead._id}>Edytuj</a>
        			else <a></a>
        			  
        "#threadtitle" #> threadHead.title &
        "#threaddelete" #> {if(isAdmin){<a href={"/forumpost/"+threadHead._id.toString + "?del=1"}>
        						<img src="/style/images/delico.png" style="float:right;" 
        						onclick="return confirm('Czy na pewno usunąć wątek?');"/></a>} else <a></a>} &
        ".firstcomment *" #> <td><div>{Unparsed(threadContent.content)}</div><hr/>
        						<p><span class="fullname">{threadHead.authorName}</span>
        						<span class="date">{Formater.formatDate(new Date(threadHead._id.getTime()))}</span>
        						<span>{editNode}</span>
        						</p></td>  &
        ".comments" #> threadContent.comments.map(comment => {
        	"div *" #> Unparsed(comment.content) & 
        	"p" #> <p><span class="fullname">{comment.authorName}</span>
        			 <span class="date">{comment.date}</span>
        			 </p> &
          "a" #> {if(isAdmin){<a href={"/forumpost/" + threadHead._id.toString + "?clr=" + comment.id.toString}>
                    <img src="/style/images/delico.png" style="float:right;" 
          			onclick="return confirm('Czy na pewno usunąć komentarz?');"/>
          				</a>} else <a></a> }
        })
        
  }
    
  //formatka dodania wpisu do wątku
  def addComment(): CssSel = {
    if (!isLoged) return "fieldset" #> <h3>Zaloguj się aby odpowiedzieć  (login i hasło uzyskasz w szkole)</h3>
    var content = ""

    def save() {
      if(isLoged){
        val user = User.currentUser.get
        val comment = Comment(ObjectId.get, user.getFullName.trim, 
        		user.id.toString, Formater.formatTime(new Date()), content)
        ForumThreadContent.update(("_id"-> threadHead.content.toString),
            ("$addToSet"-> ("comments" -> comment.mapString)))		
      val updateHeadText = "<span class='fullname'>%s<span><br/><span class='date'>%s<span>".
              format(comment.authorName ,comment.date)
      val updateObj = (("$inc" -> ("count" -> 1)) ~ ("$set" -> ("lastInfo" -> updateHeadText)))
      ForumThreadHead.update(("_id"-> threadHead._id.toString), updateObj) 
      S.redirectTo("/forumpost/"+ threadHead._id.toString)
      }
    }
    
    "#id" #> SHtml.text(threadHead._id.toString, x => x, "type" -> "hidden") &
    "#content" #> SHtml.textarea(content, x => { println(content); content = x.trim }) &   
    "#save" #> SHtml.submit("Dodaj!", save)
  }
  
  
  def deleteCommentOrThread() = {
    val deleteInfoString = "<strong>Treść niezgodna z regulaminem została usunięta przez administratora</strong>"
    if(isAdmin){
       val deleteThread = S.param("del").openOr("")
       val clearComment = S.param("clr").openOr("")
       if(deleteThread == "1"){
         println("delete thread!!!!!!!!!!")
         threadContent.delete
         performDownTagsData(threadHead.tags)
         threadHead.delete
         deleteMainPageInfo(threadHead._id.toString)
         S.redirectTo("/forum")
       }
       if(clearComment != ""){
          ForumThreadContent.update(
              ("_id"-> threadHead.content.toString)~ ("comments.id" -> clearComment),
            ("$set"-> ("comments.$.content" -> deleteInfoString)))	
         S.redirectTo("/forumpost/" + threadHead._id.toString)
       }
    }
    "#deletecommentorthread" #> <span></span>
  }
  

}
