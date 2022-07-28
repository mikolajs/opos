package eu.brosbit.opos.snippet.view

import eu.brosbit.opos.model.TestProblem
import net.liftweb.util.CssSel
import net.liftweb.util.Helpers._

class ProblemsListSn {
  def showProblems: CssSel = {
    "tr" #> TestProblem.findAll.map(p => {
      ".col1 *" #> p.title &
        ".col2 *" #> p.info &
        ".col3 *" #> <a href={"/view/checkproblem/"+p._id.toString} class="btn btn-success"><span></span> Wybierz</a>
    })
  }
}
