package pl.brosbit.snippet

import scala.xml.Unparsed
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import net.liftweb._
import http.{ S, SHtml }
import mapper.By
import _root_.pl.brosbit.model._
import Helpers._

class LoginSn {

  val userBox = User.currentUser
  def show() = {
    userBox match {
      case Full(user) =>
        "a" #> <a href="/user_mgt/logout" class="btn btn-info" role="button" title="Wyloguj" style="padding:10px">
                 <span class="glyphicon glyphicon-log-out"></span> { user.getFullName }
               </a>
      case _ =>
        "a" #> <a href="/login" role="button" class="btn btn-default" title="Zaloguj" style="padding:10px">
                 <span class="glyphicon glyphicon-user"></span>
                  Niezalogowno
               </a>
    }
  }

  def mkLogIn() = {
    var email = ""
    var pass = ""
    var pesel = ""

    def login() {
      User.findAll(By(User.email, email.trim)) match {
        case user :: other => {
          if (user.role == "t" || user.role == "a" || user.role == "d") {
            if (user.password.match_?(pass.trim)) User.logUserIn(user)
          } else {
            if (user.password.match_?(pass.trim) && pesel == user.pesel.is)
              User.logUserIn(user)
          }

        }
        case _ =>
      }
    }
    userBox match {
      case Full(user) => {
        "form" #> <span></span>
      }
      case _ => {
        "#email" #> SHtml.text(email, email = _) &
          "#password" #> SHtml.password(pass, pass = _) &
          "#pesel" #> SHtml.text(pesel, pesel = _) &
          "#mkLog" #> SHtml.submit("Zaloguj", login)
      }
    }

  }

  def showMenu() = {
    val loged_? = !userBox.isEmpty
    var viewH = "#"
    var viewC = "btn btn-default btn-lg"
    var secretariatH = "#"
    var secretariatC = "btn btn-default btn-lg"
    var educontentH = "#"
    var educontentC = "btn btn-default btn-lg"
    var registerH = "#"
    var registerC = "btn btn-default btn-lg"
    var docH = "#"
    var docC = "btn btn-default btn-lg"
    if (loged_?) {
      val user = userBox.openOrThrowException("Niemożliwe box nie jest pusty!")
      if (user.role == "n" || user.role == "a" || user.role == "d") {
        educontentH = "/educontent/index"
        educontentC = "btn btn-info btn-lg"
        registerH = "/register/index"
        registerC = "btn btn-info btn-lg"
        docH = "/documents/index"
        docC = "btn btn-info btn-lg"
      }
       if (user.role == "s" || user.role == "a" || user.role == "d") {
         secretariatH = "/secretariat/index"
         secretariatC = "btn btn-info btn-lg"
       }
    }
    "#viewA [href]" #> viewH &
      "#viewA [class]" #> viewC &
      "#secretariatA [href]" #> secretariatH &
      "#secretariatA [class]" #> secretariatC &
       "#educontentA [href]" #> educontentH &
      "#educontentA [class]" #> educontentC &
      "#registerA [href]" #> registerH &
      "#registerA [class]" #> registerC &
      "#docA [href]" #> docH &
      "#docA [class]" #> docC

  }

}