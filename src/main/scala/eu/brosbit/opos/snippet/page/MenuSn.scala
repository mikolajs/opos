/*
 * Copyright (C) 2012   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register)
*    Apache License Version 2.0, January 2004  http://www.apache.org/licenses/
 */

package eu.brosbit.opos.snippet.page

import _root_.net.liftweb._
import util._
import common._
import _root_.eu.brosbit.opos._
import model.page._
import model._
import Helpers._


class MenuSn {

  val user = User.currentUser
  val mainPageMenus = MainPageMenu.findAll

  def menu = {


        ".linkareaCol" #> mainPageMenus.map(mainPageMenu => {
      <div class="col-lg-2 linkareaCol">
        <h4>
          {mainPageMenu.name}
        </h4>
        <ul>
          {mainPageMenu.links.map(link => <li>
          <a href={link.url}>
            {link.title}
          </a>
        </li>)}
        </ul>
      </div>
    })
  }

  def bottomMenu() = {
    ".bottomLinks" #> mainPageMenus.map(mainPageMenu => {
      <div class="col-lg-2 bottomLinks">
        <h3>
          {mainPageMenu.name}
        </h3>
        <ul>
          {mainPageMenu.links.map(link => <li>
          <a href={link.url}>
            {link.title}
          </a>
        </li>)}
        </ul>
      </div>
    })
  }

  def upperMenu() = {
    val fullname = user match {
      case Full(u) => u.getFullName
      case _ => "Niezalogowany"
    }
    //val schoolInfo = MapExtraData.getMapData("schoolname")
    //val nameSchool = if(schoolInfo.contains("name")) schoolInfo("name")
   // else "ZKPiG nr 26 w Gdańsku"
    //"#schoolInfo *" #> nameSchool &
    "#loginInfo *" #> fullname
  }


 def submenu() = {
   ".login" #> (if(user.isEmpty) <a href="/login" class="btn btn-success">
     <span class="glyphicon glyphicon-user"></span> Zaloguj</a>
        else <a href="/user_mgt/logout" class="btn btn-danger">
              <span class="glyphicon glyphicon-eject"></span> Wyloguj</a>)
   }

}

