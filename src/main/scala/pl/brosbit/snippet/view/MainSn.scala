package pl.brosbit.snippet.view

import scala.xml.Unparsed
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import net.liftweb._
import http.{ S, SHtml }
import mapper.By
import pl.brosbit.model._
import Helpers._

class MainSn {
	
	def showInfo() = {
	  User.currentUser match {
	    case Full(user) => Unit
	    case _ => S.redirectTo("/")
	  }
	  "#info" #> ""
	}
}