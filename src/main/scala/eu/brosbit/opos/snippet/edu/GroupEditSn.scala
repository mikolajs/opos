package eu.brosbit.opos.snippet.edu

import eu.brosbit.opos.model.{ClassModel, User}
import eu.brosbit.opos.model.edu.{Groups, StudentInfo}
import net.liftweb.common.Empty
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JsCmds.{Run, SetHtml}
import net.liftweb.http.{S, SHtml}
import net.liftweb.util.CssSel
import net.liftweb.util.Helpers._
import net.liftweb.json.JsonDSL._
import net.liftweb.util.CanBind._

import scala.util.Try
import scala.xml.{Text, Unparsed}


class GroupEditSn {

  private val groupId = S.param("id").getOrElse("0")
  private val user = User.currentUser.openOrThrowException("Nie jesteś zalogowany")
  private val group = Groups.find("_id" -> groupId).getOrElse(Groups.create)
  private val classes = ClassModel.findAll()
  //private val classId = Try(S.param("c").getOrElse("0").toLong).getOrElse(0L)


def groupInfo: CssSel = {
    var groupName = group.name
    var groupDescription = group.description
    var groupStudents = findStudents(group.students.map(_.id))
    var studentsId = group.students.map(s => s.id.toString + ";" + s.cl).mkString(",")
    def save(): Unit = {
      if(group.authorId != 0L && group.authorId != user.id.get) return
      group.authorId = user.id.get
      group.name = groupName
      group.description = groupDescription
      //println("IDS: " + studentsId)
      group.students = getStudentsInfo(studentsId)
      //println("Students: " + group.students.mkString(","))
      group.save
      S.redirectTo("/educontent/groups")
    }
    def delete(): Unit = {
      if(group.authorId != 0L && group.authorId == user.id.get) {
        group.delete
      }
      S.redirectTo("/educontent/groups")
    }
    if(groupId != "0" && user.id.get != group.authorId) {
      S.redirectTo("/educontent/groups")
    } else {
      "#idGroup *" #> group._id.toString &
      "#name" #>  SHtml.text(groupName, x => groupName = x.trim)  &
      "#description" #> SHtml.textarea(groupDescription, x => groupDescription = x.trim) &
      "#jsonGroupList" #> SHtml.text(groupStudents, groupStudents = _) &
      "#groupListIds" #> SHtml.text(studentsId, studentsId = _) &
      "#saveGroup" #> SHtml.submit("Zapisz", save) &
      "#deleteGroup" #> SHtml.submit("Usuń", delete)
    }
  }

  def studentsInClass: CssSel = {
    def insertCmd(id: String) : JsCmd = {
      //println("insertCmd with id= " + id)
      val students = usersToJson(getStudentsFormClass(id))
      //println("STUDENTS: \n" + students)
      SetHtml("studentsList", Text(students)) & Run("groupEdit.reloadClassStudents()")
    }
    val options: List[(String, String)] = classes.map(c => (c.id.get.toString, c.shortInfo()))
    "select" #> SHtml.ajaxSelect(options, Empty, insertCmd) &
    "#studentsList *" #> Unparsed(usersToJson(getStudentsFormClass(classes.head.id.get.toString)))
  }

  def getClasses: CssSel = {
    "option" #> classes.map(c =>{
      <option id={"c_" + c.id.get.toString}>{c.classString()}</option>
    })
  }

  private def findStudents(ids:List[Long]) = usersToJson(User.findAll().filter(u => ids.contains(u.id.get)))

  private def getStudentsFormClass(classId:String)= {
    User.findAll().filter(u => u.classId.get.toString == classId && u.role.get == "u")
  }
  private def usersToJson(u:List[User]) = {
    "[" +
    u.map(u => s"""{"id":${u.id.get}, "fname":"${u.firstName.get}", "lname":"${u.lastName.get}", "email":"${u.email.get}"  }""").mkString(",") +
    "]"
  }
  private def getStudentsInfo(studentsId:String) = {
    studentsId.split(',').map(_.split(';'))
      .filter(_.length == 2).map(si => StudentInfo(Try(si(0).trim.toLong).getOrElse(0L), si(1).trim)).toList
  }
}
