/*
 * Copyright (C) 2011   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register - Wirtualny Dziennik)
 *   Licence AGPL v3 see http://www.gnu.org/licenses/
 */

package pl.brosbit.snippet.page

import _root_.scala.xml.{NodeSeq}
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.pl.brosbit.model.page._
import _root_.pl.brosbit.model._
import Helpers._

trait UsersOperations {

  var user = User.create
  var isLoged = false
  var isTeacher = false
  var isAdmin = false
  var userID = 0L
  val currentUserName = User.currentUser match {
    case Full(u) => {
      isLoged = true
      isTeacher = if (u.role.get == "n" || u.role.get == "a") true else false
      isAdmin = if (u.role.get == "a") true else false
      userID = u.id.get
      user = u
      u.firstName + " " + u.lastName
    }
    case _ => "Niezalogowany"
  }

  def loginInfo() = {
    val node = if (isLoged) <span id="login">
      <a href="/user_mgt/logout">
        <span>
          {currentUserName}
        </span>
      </a>
      <a href="/user_mgt/change_password">
        &nbsp; &nbsp; &nbsp; &nbsp; <img src="/style/images/dice.png" title="Zmień hasło"/>
      </a>
    </span>
    else <a href="/user_mgt/login">
      <span>Niezalogowany</span>
    </a>
    "a" #> node
  }

  def isPostOwner(id: Long) = id == userID

}
 