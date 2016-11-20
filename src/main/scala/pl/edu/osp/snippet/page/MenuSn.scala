/*
 * Copyright (C) 2012   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register)
*    Apache License Version 2.0, January 2004  http://www.apache.org/licenses/
 */

package pl.edu.osp.snippet.page

import scala.xml.{Unparsed}
import _root_.net.liftweb._
import util._
import common._
import _root_.pl.edu.osp._
import model.page._
import model._
import _root_.net.liftweb.mapper.By
import _root_.net.liftweb.http.{S, SHtml}
import Helpers._
import _root_.net.liftweb.json.JsonDSL._


class MenuSn {

  val user = User.currentUser


  def links = {
    val mainPageLinks = MainPageLinks.findAll match {
      case head :: list => head
      case _ => MainPageLinks.create
    }
    ".grid_3" #> mainPageLinks.links.map(linkGroup => {
      <div class="grid_3">
        <h3>
          {linkGroup.name}
        </h3>
        <ul>
          {linkGroup.links.map(link => <li>
          <a href={link.url}>
            {link.title}
          </a>
        </li>)}
        </ul>
      </div>
    })
  }



  def logIn() = {
    var email = ""
    var pass = ""
    var pesel = ""

    def mkLog() = {
      if (user.isEmpty) {
        User.findAll(By(User.email, email.trim)) match {
          case u :: other => {
            User.logUserIn(u)
          }
          case _ =>
        }
      }


    }
    user match {
      case Full(u) => {
        "form" #> <a title="WYLOGUJ!" href="/user_mgt/logout">
          <button type="button" class="btn btn-info">
            <span class="glyphicon glyphicon-log-out"></span>{u.getFullName}
          </button>
        </a>
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

