package eu.brosbit.opos.snippet.edu

import eu.brosbit.opos._
import model._
import _root_.net.liftweb._
import common._
import util._

trait RoleChecker {

  def isTeacher: Boolean = {
    User.currentUser match {
      case Full(user) => {
        var r = user.role.get
        (r == "t" || r == "m" || r == "a")
      }
      case _ => {
        false
      }
    }
  }


  def isAdmin = User.currentUser match {
    case Full(user) => user.role.get == "a"
    case _ => false
  }
}