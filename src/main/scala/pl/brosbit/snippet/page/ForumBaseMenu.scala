package pl.brosbit.snippet.page

import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.pl.brosbit.model._
import _root_.net.liftweb.mapper.{ Descending, OrderBy, By }
import page._
import Helpers._
import _root_.net.liftweb.json.JsonDSL._

trait ForumBaseMenu {
    
    def allTags() = {

        val spans =  ForumTag.findAll.filter(tag => tag.count > 0).map(tag => {
            <span ><a href={ "/forum?tags=" + tag.tag}>
            { tag.tag + " (" + tag.count.toString + ")"}</a></span>
        })
        val all = <span ><a href="/forum" >Wszystkie</a></span> 
            all ++ spans  
    }

     def availableTags() = {
    	
       val allTagsList = ForumTag.findAll.map(tag => tag.tag).toList
       val subjects = SubjectName.findAll(By(SubjectName.scratched, false)).map(_.name.is)
       val classes = ClassModel.findAll(By(ClassModel.scratched, false)).map(classMod => 
       	"kl_" + classMod.classString())
       	
       val allTagsReturn  = allTagsList.filter(tag =>
           !subjects.contains(tag)).filter(tag => !subjects.contains(tag)) ::: subjects ::: classes
       	
       allTagsReturn.map(tag => (tag,tag))
  }
     
     protected def performDownTagsData(tags:List[String]) {
        tags.foreach(x => ForumTag.update(("tag" -> x), ("$inc" -> (x -> -1))))
        val toDel = ("count" -> ("$lt" -> 1))
        println("=================== Delete tags in delete thread ============")
        ForumTag.delete(toDel)
    }
 
}