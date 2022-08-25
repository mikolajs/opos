package eu.brosbit.opos.model.edu

import _root_.net.liftweb.mongodb._
import org.bson.types.ObjectId
import scala.util.parsing.json.JSONFormat._

/* Typ pytania:
 * 1. brak fake i jeden lub kikla answers - pytanie otwarte - wpisuje się słowo, które ma pasować do jednej z odpowiedzi
 * 2. jeden lub kilka fakes i jeden answers - pytanie zamknięte jednokrotnego wyboru
 * 3. jeden lub kilka fake i więcej niz jeden answers - pytanie wielokrotnego wyboru
 * 4. brak fake i answers pytanie otwarte bez sprawdzania - na teście okno textedit
 */

//authors - last is older
trait ListToJsonString {
  def mkStringFromList(col:List[String]): String = if(col.isEmpty) "" else "\"" + col.mkString("\",\"") + "\""
}

object QuizQuestion extends MongoDocumentMeta[QuizQuestion] {
  override def collectionName = "questions"
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = new QuizQuestion(ObjectId.get, 0L, 1, 1, 0L, 0, "", "", "", Nil, "", Nil, "")
}

case class QuizQuestion (var _id: ObjectId, var authorId: Long, var dificult: Int, var lev: Int,
                        var subjectId: Long, var nr: Int, var subjectName: String, var info: String,
                        var question: String, var answers: List[String], var department: String,
                        var fake: List[String], var hint: String) extends MongoDocument[QuizQuestion] with ListToJsonString {
  def meta = QuizQuestion
  def strJson =
    s"""{"_id":"${_id.toString}", "difficult":"${dificult}", "lev":"$lev", "subjectName":"$subjectName",
       |"info":"${quoteString(info)}", "question":"${quoteString(question.trim)}", "department":"${quoteString(department)}",
       |"answers":[${mkStringFromList(answers)}], "fake":[${mkStringFromList(fake)}], "hint":"${quoteString(hint)}"}
       |""".stripMargin
}
