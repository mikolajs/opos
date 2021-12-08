package eu.brosbit.opos.snippet.edu

import eu.brosbit.opos.model.{Problems, User}
import net.liftweb.util.CssSel
import net.liftweb.util.Helpers._
import net.liftweb.http.{S, SHtml}

import scala.xml.Text

class EditProblemSn {
  var id = S.param("id").getOrElse("0")
  var user = User.currentUser.getOrElse(S.redirectTo("/login"))
  println("im a user " + user.id.get)
  val problem = if(id == "0") Problems.create else Problems.find(id).getOrElse(Problems.create)
  if(problem.author != 0 && problem.author != user.id.get ) S.redirectTo("/educontent/problems")
  else if(problem.author == 0L) problem.author = user.id.get
  def edit:CssSel = {
    var idP = problem._id.toString
    var titleP = problem.title
    var infoP = problem.info
    var descriptionP = problem.description

    def deleteProblem() {
      problem.delete
    }
    def saveProblem() {
      if(titleP.trim.isEmpty) titleP = "Brak nazwy!!!!"
      problem.info = infoP
      problem.description = descriptionP
      problem.title = titleP
      problem.save
    }
    "#idProblem" #> SHtml.text(idP, idP = _) &
    "#titleProblem" #> SHtml.text(titleP, titleP = _ ) &
    "#infoProblem" #> SHtml.text(infoP, infoP = _) &
    "#descriptionProblem" #> SHtml.textarea(descriptionP, descriptionP = _) &
    "#deleteProblem" #> SHtml.button(Text("Usuń"), deleteProblem, "title" -> "Usuń") &
    "#saveProblem" #> SHtml.submit("Zapisz", saveProblem, "title" -> "Zapisz")
  }
}
