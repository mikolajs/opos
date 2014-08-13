
package pl.brosbit.snippet.edu

import java.util.Date
import scala.xml.Unparsed
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import net.liftweb._
import http.{ S, SHtml }
import mapper.{ OrderBy, Descending, By }
import pl.brosbit.model._
import edu._
import Helpers._

class MainSn {

    def showCourses() = {

        ".courseItem" #> Course.findAll.map(course => {
            "h2" #> <h2>{ course.title }<span class="text-muted">{ " - klasa " + course.classInfo } </span></h2> &
                ".courseInfo *" #> course.descript &
                ".btn [href]" #> ("/course/" + course._id.toString) &
                ".img-responsive [src]" #> course.img
        })

    }

    def logIn() = {
        var email = ""
        var pass = ""
        var pesel = ""

        def mkLog() = {
            User.findAll(By(User.email, email.trim)) match {
                case user :: other => {
                    User.logUserIn(user)
                    S.redirectTo("/index")
                }
                case _ =>
            }
        }
        User.currentUser match {
            case Full(user) => {
               "form" #>  <a title="WYLOGUJ!" href="/user_mgt/logout">
               	<button type="button" class="btn btn-info btn-lg">
               	<span class="glyphicon glyphicon-log-out"></span> { user.getFullName }</button></a>
            }
            case _ => {
                "#email" #> SHtml.text(email, email = _) &
                    "#password" #> SHtml.password(pass, pass = _) &
                    "#pesel" #> SHtml.text(pesel, pesel = _) &
                    "#mkLog" #> SHtml.submit("Zaloguj", mkLog)
            }
        }

    }
}
