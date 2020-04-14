package eu.brosbit.opos.api

import net.liftweb.common.Full
import net.liftweb.http.NotFoundResponse


object Exports {

  def slides(what: String) = what match {
    case "slides" => {
      ImportExportSlides.zip()
    }
    case _ => Full(NotFoundResponse("Not found"))
  }



}
