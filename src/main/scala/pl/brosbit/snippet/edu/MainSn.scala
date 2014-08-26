
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
                ".btn [href]" #> ("/course/" + course._id.toString) //&
                //".img-responsive [src]" #> course.img
        })

    }
}
