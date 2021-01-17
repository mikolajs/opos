package eu.brosbit.opos.snippet.edu

import eu.brosbit.opos.model.User
import eu.brosbit.opos.model.edu.Groups
import net.liftweb.util.CssSel
import net.liftweb.util.Helpers._
import net.liftweb.json.JsonDSL._

class GroupsSn {

  val user = User.currentUser.openOrThrowException("Nie jesteś zalogowany")
  val groups = Groups.findAll("authorId" -> user.id.get)

  def showGroups: CssSel = {
    "#tbody" #> groups.map(group => {
      <tr><td>{group.name}</td><td>{group.description}</td><td>{group.students.length.toString}</td>
        <td><a class="btn btn-success" href={"/educontent/groupedit/"+group._id.toString}>Edytuj</a></td></tr>
    })
  }
}
