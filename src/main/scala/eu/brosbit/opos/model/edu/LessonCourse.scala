package eu.brosbit.opos.model.edu

import _root_.net.liftweb.mongodb._
import org.bson.types.ObjectId
import scala.util.parsing.json.JSONFormat._

case class LessonContent(what: String, id: String, title: String, descript: String) {
  import net.liftweb.json.Serialization.write
  import net.liftweb.json.DefaultFormats
  implicit val formats = DefaultFormats
  def forJSONStr = write(this)
 /* def forJSONStr = "{\"what\":\"" + what + "\",\"id\":\"" + id + "\",\"title\":\"" +
    title + "\",\"descript\":\"" + descript + "\"}"*/
}

// what: headword - h,  quest - q


//case class LessonItem(what: String, id: String, title: String, descript: String)

object LessonCourse extends MongoDocumentMeta[LessonCourse] {
  override def collectionName = "lessons"

  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer

  def create = new LessonCourse(ObjectId.get, 99, 0L, "", "", "",
    new ObjectId("000000000000000000000000"), Nil)
}

case class LessonCourse(var _id: ObjectId, var nr: Int, var authorId: Long,
                        var chapter: String, var title: String,
                        var descript: String, var courseId: ObjectId, var contents: List[LessonContent])
  extends MongoDocument[LessonCourse] with ListToJsonString {
  def meta = LessonCourse
  def strJson =
    s"""{"_id":"${_id.toString}", "title":"${quoteString(title)}", "descript":"${quoteString(descript)}", "chapter":"${quoteString(chapter)}",
       |"courseId":"$courseId", "contents": [${contents.map(_.forJSONStr).mkString(",")}], "nr":"$nr"}
       |""".stripMargin
  def json = {
    import net.liftweb.json.Serialization.write
    import net.liftweb.json.DefaultFormats
    implicit val formats = DefaultFormats
    write(this)
  }
}
