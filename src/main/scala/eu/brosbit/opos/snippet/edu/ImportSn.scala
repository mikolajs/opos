package eu.brosbit.opos.snippet.edu

import eu.brosbit.opos.api.Exports
import eu.brosbit.opos.lib.Zipper
import net.liftweb.common.{Box, Empty, Full}
import net.liftweb.http.{FileParamHolder, S, SHtml}
import net.liftweb.json.DefaultFormats
import net.liftweb.util.CssSel
import net.liftweb.util.Helpers._

import java.nio.charset.StandardCharsets

case class ElementsInZip(var docs:Int = 0, var presentations: Int = 0, var videos: Int = 0, var questions: Int = 0, var lessons: Int = 0,
                         var files: Int = 0)
case class ElementsJson(var docs:String = "", var presentations: String = "", var videos: String = "", var questions: String = "",
                        var lessons: String = "", var files: String = "")

class ImportSn {

  private def mkImport(data: Array[Byte], eiz: ElementsInZip) = {
    val zipper = new Zipper()
    val mapFiles = zipper.fromZipJsonAndBinary(data)
    val mapKeys = mapFiles.keys
    mapKeys.foreach(key => {
      val fileData = mapFiles(key)
      key match {
        case k if k == Exports.JsonFileNames.Documents.toString => insertDocuments(mapFiles(key), eiz)
        case k if k == Exports.JsonFileNames.Presentations.toString => insertPresentation(mapFiles(key), eiz)
        case k if k == Exports.JsonFileNames.Videos.toString => insertVideos(mapFiles(key), eiz)
        case k if k == Exports.JsonFileNames.Questions.toString => insertQuestions(mapFiles(key), eiz)
        case k if k == Exports.JsonFileNames.Lessons.toString => insertLessons(mapFiles(key), eiz)
        case k if k == "filesDir" => insertFiles(mapFiles(key), eiz)
      }
    })
  }

  def importing: CssSel = {
    val elementsInZip = ElementsInZip()
    var error = ""
    def mkUpload(zipFile:FileParamHolder): Unit = {
      if(zipFile.fileName != "export.zip") error = "Błędna nazwa pliku"
      else mkImport(zipFile.file, elementsInZip)
    }

    def emptyFun(){}

    "#filezip" #> SHtml.fileUpload( zipFile => mkUpload(zipFile)) &
    "#docs *" #> elementsInZip.docs.toString &
    "#presentations *" #> elementsInZip.presentations.toString &
    "#videos *" #> elementsInZip.videos.toString &
    "#questions *" #> elementsInZip.questions.toString &
    "#lessons *" #> elementsInZip.lessons.toString &
    "#files *" #> elementsInZip.files.toString &
    "#save"#> SHtml.submit("Wczytaj", () => emptyFun())
  }

  def test: CssSel = {
    val elementsJson = ElementsJson()
    var fileParamHolder:Box[FileParamHolder] = Empty
    var error = ""
    def save(): Unit = {
      if(fileParamHolder.isEmpty) {
        println("BRAK PLIKU ZIP!")
        return
      }
      val zipFile = fileParamHolder.openOrThrowException("Brak pliku")
      println("start import zip file: " + zipFile.fileName)
      if(zipFile.fileName != "export.zip") error = "Błędna nazwa pliku"
      else mkImportTest(zipFile.file, elementsJson)
      //println(elementsJson)
      S.notice(setInfo(elementsJson))
    }



    "#filezip" #> SHtml.fileUpload( zipFile => fileParamHolder = Full(zipFile)) &
      "#save"#> SHtml.submit("Wczytaj", () => save())
  }

  private def mkImportTest(data: Array[Byte], ej: ElementsJson): Unit = {
    val zipper = new Zipper()
    val mapFiles = zipper.fromZipJsonAndBinary(data).filterNot(_._1.take(10) == "dane/pliki")
    val mapKeys = mapFiles.keys
    def mkJsonString(a:Array[Byte]) = new String(a, 0, a.length, StandardCharsets.UTF_8)
    mapKeys.foreach(key => {
      val newKey = key.split('/').last
      newKey match {
        case k if k == Exports.JsonFileNames.Documents.toString => ej.docs = mkJsonString(mapFiles(key))
        case k if k == Exports.JsonFileNames.Presentations.toString => ej.presentations = mkJsonString(mapFiles(key))
        case k if k == Exports.JsonFileNames.Videos.toString => ej.videos = mkJsonString(mapFiles(key))
        case k if k == Exports.JsonFileNames.Questions.toString => ej.questions = mkJsonString(mapFiles(key))
        case k if k == Exports.JsonFileNames.Lessons.toString => ej.lessons = mkJsonString(mapFiles(key))
        case k if k == Exports.JsonFileNames.Files.toString => ej.files = mkJsonString(mapFiles(key))
        case _ => Unit
      }
    })
   // println(ej)
  }
  private def setInfo(json: ElementsJson): String = {
    s""" {${json.docs}, ${json.presentations}, ${json.videos},  ${json.questions}, ${json.lessons}, ${json.files}"""
  }
//TODO: Implement!
  private def insertDocuments(bytes: Array[Byte], zip: ElementsInZip): Unit = {

  }
  //TODO: Implement!
  private def insertLessons(bytes: Array[Byte], zip: ElementsInZip): Unit ={

  }
  //TODO: Implement!
  private def insertQuestions(bytes: Array[Byte], zip: ElementsInZip) = {

  }
  //TODO: Implement!
  private def insertVideos(bytes: Array[Byte], zip: ElementsInZip): Unit = {

  }
  //TODO: Implement!
  private def insertPresentation(bytes: Array[Byte], zip: ElementsInZip): Unit = {

  }
  //TODO: Implement!
  private def insertFiles(bytes: Array[Byte], zip: ElementsInZip) = {

  }

}
