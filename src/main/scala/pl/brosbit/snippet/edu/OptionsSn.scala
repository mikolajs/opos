package pl.brosbit.snippet.edu

import pl.brosbit._
import model._
import edu._
import scala.xml._
import _root_.net.liftweb._
import common._
import util._
import mapper.{OrderBy, Ascending}
import http.{S,SHtml}
import json.JsonDSL._
import json.JsonAST.JObject
import json.JsonParser
import org.bson.types.ObjectId
import Helpers._
import Helpers._

class OptionsSn extends  BaseResourceSn  {

    	def showAllSubjects() = {
    	    val subjectsAll = SubjectName.findAll(OrderBy(SubjectName.nr, Ascending))
    	    		.filter(s => !subjectTeach.exists(t => t.id == s.id.is))
    	    "li *" #>  subjectsAll.map(  s =>
    	           (<button  class="btn btn-info"  onclick="subjects.addSubject(this)">
    	        		   <span class="glyphicon glyphicon-plus-sign"></span></button>
    	            ++ <span class="subjectName" id={"id_" + s.id.toString}>{s.name}</span>)
    	            )
    	}
    	
    	def showMySubjects() = {
    	    
    	    "li" #>  subjectTeach.map(s => (<li class="list-group-item"><button  class="btn btn-danger"  onclick="subjects.removeSubject(this)">
    	        		   <span class="glyphicon glyphicon-remove-sign"></span></button>
    	    				<span class="subjectName"  id={"id_" + s.id.toString}>{s.name}</span></li>)
    	            )
    	}
    	
    	def formEdit() = {
    	    var data = ""
    	    def save() {
    	        var notdeleted = true
    	         data.split('|').map(d => {
    	            val  sub = d.split(";")
    	            if(sub.length == 3) {
    	                val id = tryo(sub.head.toLong).openOr(0L)
    	                val nr = tryo(sub(1).toInt).openOr(0)
    	                if(id != 0L && nr != 0) {
    	                    if(notdeleted) {SubjectTeach.delete("authorId"-> user.id.is); notdeleted = false}
    	                     val subT = SubjectTeach.create
    	                     subT.authorId = user.id.is
    	                     subT.name = sub.last
    	                     subT.id = id
    	                     subT.prior = nr 
    	                     subT.save
    	                }
    	            }
    	         })
    	    }
    	    "#dataContent" #> SHtml.text("", data = _, "display" -> "none") &
    	    "#save" #> SHtml.button(<span class="glyphicon glyphicon-floppy-save"> Zapisz zmiany!</span> , save,  "class" -> "btn btn-success btn-lg")
    	}
    	
    	def deleteDepartment() = {
    	  var department = ""
    	  var subjectId = ""
    	  def delete() {
    	    subjectTeach.find(st => st._id.toString() == subjectId) match {
    	      case Some(subjectT) => {
    	        subjectT.departments = subjectT.departments.filterNot(d => d == department)
    	        subjectT.save
    	      }
    	      case _ =>
    	    }
    	  }
    	  
    	  "#departmentName" #> SHtml.text(department, department = _) & 
    	  "#subject" #> SHtml.text(subjectId, subjectId = _ )
    	}
    	
    	def showDepartments() = {
    	  "#subjectDepartment" #> subjectTeach.map(subj => {
    	    ".panel-heading *" #> Text(subj.name) &
    	    ".panel-heading [name]" #> subj._id.toString() &
    	    "ul" #> subj.departments.map(d =>{
    	      "li *" #> (<span>d</span> ++ <button type="button" class="btn btn-danger btn-lg"  
    	      onclick="subjects.delDepart(this)">
    	      <span class="glyphicon glyphicon-remove-sign"></span>
    	      </button>)
    	    })
    	  })
    	  
    	}
}