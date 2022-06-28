package eu.brosbit.opos.judge

import java.io.File
import scala.collection.mutable
import eu.brosbit.opos.lib.ConfigLoader

case class CodeToTest(id:String, code:String, lang:String, testDataInput:List[String])

class CheckCodeToTest {
  val toTestQueue:mutable.Queue[CodeToTest] = mutable.Queue[CodeToTest]()

  val mainPath:String = getPathForTestFiles

  def addTest(id: String, code:String, lang: String, testDataInput:List[String]):Unit =
    toTestQueue += CodeToTest(id, code, lang, testDataInput)

  def getNextTest:Option[CodeToTest] = if (toTestQueue.isEmpty) None else Some(toTestQueue.dequeue())

  private def getTestFromDir() = {
    getListOfFiles(new File(mainPath), onlyFiles = false)
    .foreach(d =>
      if (d.exists() && d.isDirectory) toTestQueue += mkTestFromDir (d)
    )
  }

  def mkTestFromDir(dir:File): CodeToTest = {
    var testData: List[String] = Nil
    var code = ""
    var lang = ""
    getListOfFiles(dir).foreach(f =>
      if (f.isFile && f.canRead && f.getName.split('.').last == "txt")
        testData = loadFromFile(f) :: testData
      else if (f.isFile && f.canRead && f.getName.split('.').head == "test") {
        code = loadFromFile(f)
        lang = f.getName.split('.').last
      }
    )
    CodeToTest(dir.getName, code, lang, testData)
  }

  private def loadFromFile(f:File) = {
    val source = scala.io.Source.fromFile(f)
    val s = source.mkString
    source.close()
    s
  }

  private def getListOfFiles(d: File, onlyFiles: Boolean = true):List[File] = {
    if (d.exists && d.isDirectory)
      if (onlyFiles)  d.listFiles.filter(_.isFile).toList
      else d.listFiles.filter(_.isDirectory).toList
    else List[File]()
  }

  private def getPathForTestFiles:String = ConfigLoader.judgeDir
}
