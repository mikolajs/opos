package pl.brosbit.snippet.admin

import pl.brosbit.model.page.ExtraData
import net.liftweb.http.SHtml
import net.liftweb.util.Helpers._

/**
 * dodawanie codu z google
 */
class AdminSearchGoogleSn {

def show() = {

  var code = ExtraData.getData("googlesearchcode")
  def save() {
    ExtraData.updateKey("googlesearchcode", code)
  }
  "#code" #> SHtml.textarea(code, code = _) &
  "#save" #> SHtml.submit("Zapisz", save)
}

}
