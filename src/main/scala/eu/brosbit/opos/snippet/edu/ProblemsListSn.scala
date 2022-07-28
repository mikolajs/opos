package eu.brosbit.opos.snippet.edu

import eu.brosbit.opos.model.{TestProblem, User}
import net.liftweb.http.S
import net.liftweb.json.JsonDSL._
import net.liftweb.util.CssSel
import net.liftweb.util.Helpers._

class ProblemsListSn {

  var userId = User.currentUser.getOrElse(S.redirectTo("/login")).id
  def showProblems: CssSel = {
   "tr" #> TestProblem.findAll("author"->userId.get ).map(p => {
      ".col1 *" #> p.title &
      ".col2 *" #> p.info &
      ".col3 *" #> <a href={"/educontent/editproblem/"+p._id.toString} class="btn btn-success"><span></span> Edytuj</a> &
      ".col4 *" #> <a href={"/educontent/showproblemresults/"+p._id.toString + "/0"}
                      class="btn btn-success"><span></span> Rozwiązania</a>
   })
  }

}
