package pl.brosbit.snippet.edu

import _root_.net.liftweb.util._
import Helpers._
import net.liftweb.http.S

/**
 * Created by ms on 11.08.15.
 */
class CheckExamSn {
  val ansId = S.param("id").openOr("")
  if(ansId.isEmpty) S.redirectTo("/educontent/exams")

 def showAnswers() = {
   "nene*" #> "none"
 }

 def showPupil() = {
   "nene*" #> "none"
 }

}
