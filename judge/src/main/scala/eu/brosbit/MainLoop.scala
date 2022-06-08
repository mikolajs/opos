package eu.brosbit
import akka.stream.scaladsl.JavaFlowSupport.Source

import scala.collection.mutable.Queue
import java.nio.charset
import java.nio.file.*
import java.nio.charset.StandardCharsets
import java.io.File

case class CodeToTest(id:String, code:String, lang:String, testDataInput:List[String])

class MainLoop {
  val toTestQueue:Queue[CodeToTest] = Queue()
  
  val mainPath = getPathForTestFiles 

  def addTest(id: String, code:String, lang: String, testDataInput:List[String]) =
    toTestQueue += CodeToTest(id, code, lang, testDataInput)

  def getNextTest:Option[CodeToTest] = if toTestQueue.isEmpty then None else Some(toTestQueue.dequeue())

  def getTestFromDir (): Unit =
    val dirs = getListOfFiles(File(mainPath), false)
    dirs.foreach(d =>
      if(d.exists() && d.isDirectory) then
        toTestQueue += mkTestFromDir(d)
    )

  def mkTestFromDir(dir:File): CodeToTest =
    var testData:List[String] = Nil
    var code = ""
    var lang = ""
    getListOfFiles(dir).foreach(f =>
      if f.isFile && f.canRead && f.getName.split('.').last == "txt" then
        testData = scala.io.Source.fromFile(f).mkString::testData
      else if f.isFile && f.canRead && f.getName.split('.').head == "test" then
        code = scala.io.Source.fromFile(f).mkString
        lang = f.getName.split('.').last
    )
    CodeToTest(dir.getName, code, lang, testData)


  private def getListOfFiles(d: File, onlyFiles: Boolean = true):List[File] =
    if d.exists && d.isDirectory then
      if onlyFiles then d.listFiles.filter(_.isFile).toList
      else d.listFiles.filter(_.isDirectory).toList
    else List[File]()

  private def getPathForTestFiles:String =
    scala.io.Source.fromFile("/etc/osp/judge.cfg").getLines().map(line =>
      val arr = line.split('=')
      if(arr.size == 2 && arr(0) == "path") return arr(1)
    )
    return "/home/admin/judge"

}
