package eu.brosbit.opos.snippet.edu

import eu.brosbit.opos.api.Exports.saveExportedFiles
import eu.brosbit.opos.model.User
import net.liftweb.http.S
import net.liftweb.util.CssSel
import net.liftweb.util.Helpers._

class ExportSn {
  val user: User = User.currentUser.getOrElse(S.redirectTo("/"))
  def exportedFiles():CssSel = {
    val filesLinks = saveExportedFiles(user)
    "filesExported *" #> filesLinks.map(link =>{
       <div><a href="link">{link.split("/").last}</a></div>
    })
  }
}
