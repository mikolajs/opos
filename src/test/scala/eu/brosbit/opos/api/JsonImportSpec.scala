package eu.brosbit.opos.api

import eu.brosbit.opos.api.JsonExportImport.{CourseImport, DocumentsImport, LessonImport, PresentationImport, ProblemImport, QuestionImport, VideoImport}
import eu.brosbit.opos.model.edu.LessonContent
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class JsonImportSpec extends AnyFlatSpec with Matchers {

  "JSON Import/export case classes" should "create string Problem" in {
    val pi = ProblemImport("2343423423423423432", "Temat", "opis mój", "informacje", "323\r\n3434", "567\r\n7655")
    val s = pi.toJson
    //println(s)
    val pi2 = JsonExportImport.fromJsonProblem(s)
    pi._id should be (pi2._id)
    pi.title should be (pi2.title)
    pi.info should be (pi2.info)
    pi.inputs should be (pi2.inputs)
    pi.description should be (pi2.description)
    pi.expectedOutputs should be (pi2.expectedOutputs)
  }

  it should "create string Question" in {
    val q1 = QuestionImport("23432423423", 2, 1, "Nazywa", "opiśik", "Pytanie na śniadanie", "informatyka",
      List("abc", "gde"), List("be", "nie"), "echo")
    val s = q1.toJson
    //println(s)
    val q2 = JsonExportImport.fromJsonQuestion(s)
    q1.info should be (q2.info)
    q1.hint should be (q2.hint)
    q1.department should be (q2.department)
    q1.lev should be (q2.lev)
    q1.dificult should be (q2.dificult)
    q1.answers should be (q2.answers)
    q1.fake should be (q2.fake)
  }

  it should "create string Document" in {
    val doc1 = DocumentsImport("23456765432234", "Tytułowy dokument", "to mamy święty opiś", "religia", "fizyka atomowa", "zawartość to jest", 2)
    val j = doc1.toJson
    //println(j)
    val doc2 = JsonExportImport.fromJsonDocument(j)
    doc1._id should be (doc2._id)
    doc1.lev should be (doc2.lev)
    doc1.title should be (doc2.title)
    doc1.department should be (doc2.department)
    doc1.descript should be (doc2.descript)
    doc1.content should be (doc2.content)
  }

  it should "create string Lesson" in {
    val cont = List(
      LessonContent("a", "3243256", "tytule", "opsek"),
      LessonContent("b", "33434243256", "tytule", "opsek"),
      LessonContent("e", "32343243256", "tydkke", "daskdk"))

    val les1 = LessonImport("234234252354", "tyttułeek", "ospis oskdkk dowy kdk", "elektrostatyka stosowana", "343345453453454", cont ,22)
    val j = les1.toJson
    //println(j)
    val les2 = JsonExportImport.fromJsonLesson(j)
    les1._id should be (les2._id)
    les1.title should be (les2.title)
    les1.nr should be (les2.nr)
    les1.descript should be (les2.descript)
    les1.chapter should be (les2.chapter)
    les1.courseId should be (les2.courseId)
    les1.contents.length should be (les2.contents.length)
    les1.contents.mkString(",") should be (les2.contents.mkString(","))
  }

  it should "create string Video" in {
    val vid1 = VideoImport("34534534", "tytlułekdś", "opisek na pisek", "elektomontaż", "bambobudia", "http://hint.com", 1, true, "png", "/home/ms/info")
    val j = vid1.toJson
    //println(j)
    val vid2 = JsonExportImport.fromJsonVideo(j)
    vid1.title should be (vid2.title)
    vid1._id should be (vid2._id)
    vid1.lev should be (vid2.lev)
    vid1.link should be (vid2.link)
    vid1.oldPath should be (vid2.oldPath)
    vid1.mime should be (vid2.mime)
    vid1.department should be (vid2.department)
    vid1.onServer should be (vid2.onServer)
    vid1.descript should be (vid2.descript)
    vid1.subjectName should be (vid2.subjectName)
  }

  it should "create string Presentation" in {
    val pre1 = PresentationImport("324434534", "tytułowe js", "ospkksdjkf, dkfj", "informatyka", "algorytmy", "tu są slajdy", 1)
    val j = pre1.toJson
    //println(j)
    val pre2 = JsonExportImport.fromJsonPresentation(j)
    pre1._id should be (pre2._id)
    pre1.lev should be (pre2.lev)
    pre1.title should be (pre2.title)
    pre1.descript should be (pre2.descript)
    pre1.slides should be (pre2.slides)
    pre1.subjectName should be (pre2.subjectName)
    pre1.department should be (pre2.department)
  }

  it should "create string Course" in {
    val c1 = CourseImport("23499439", "Tytułowy kurs", List("4554245", "43543523452", "234234234"), "fizyka", "opis", "pliczak/img")
    val j = c1.toJson
    val c2 = JsonExportImport.fromJson[CourseImport](j)
    //println(j)
    c1._id should be (c2._id)
    c1.title should be (c2.title)
    c1.img should be (c2.img)
    c1.descript should be (c2.descript)
    c1.subjectName should be (c2.subjectName)
    c1.chapters.length should be (c2.chapters.length)
    c1.chapters.mkString(",") should be (c2.chapters.mkString(","))
  }
}
