package eu.brosbit.opos.model.edu

import _root_.net.liftweb.mongodb._
import org.bson.types.ObjectId

/* Typ pytania: 
 * 1. brak fake i jeden lub kikla answers - pytanie otwarte - wpisuje się słowo, które ma pasować do jednej z odpowiedzi
 * 2. jeden lub kilka fakes i jeden answers - pytanie zamknięte jednokrotnego wyboru
 * 3. jeden lub kilka fake i więcej niz jeden answers - pytanie wielokrotnego wyboru
 * 4. brak fake i answers pytanie otwarte bez sprawdzania - na teście okno textedit
 */

//authors - last is older
object QuizQuestion extends MongoDocumentMeta[QuizQuestion] {
  override def collectionName = "questions"

  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer

  def create = new QuizQuestion(ObjectId.get, 0L, 1, 1, 0L, 0, "", "", "", Nil, "", Nil)
}

case class QuizQuestion( _id: ObjectId, var authorId: Long, var dificult: Int, var lev: Int,
                        var subjectId: Long, var nr: Int, var subjectName: String, var info: String,
                        var question: String, var answers: List[String], var department: String,
                        var fake: List[String]) extends MongoDocument[QuizQuestion] {
  def meta = QuizQuestion
}
