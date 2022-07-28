package eu.brosbit.opos.snippet.edu

import eu.brosbit.opos.lib.{Formater, TestProblemRunner}
import eu.brosbit.opos.model.{TestProblem, TestProblemTry, User}
import eu.brosbit.opos.snippet.CheckProblemSn
import net.liftweb.util.CssSel
import net.liftweb.util.Helpers._
import net.liftweb.http.{S, SHtml}
import net.liftweb.json.JsonDSL._

import java.util.Date
import java.util.zip.DataFormatException
import scala.xml.{Text, Unparsed}

class CheckProblemTeacherSn extends CheckProblemSn {
  override val baseDir = "/educontent"
}
