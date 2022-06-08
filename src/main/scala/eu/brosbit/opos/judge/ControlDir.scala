package eu.brosbit.opos.judge

import java.nio.charset
import java.io.File

import java.nio.charset.StandardCharsets
import eu.brosbit.opos.lib.ConfigLoader
import java.io.FileWriter
import java.io.PrintWriter

object ControlDir {
  val mainDir = ConfigLoader.judgeDir
}

  class ControlDir(id: String){

  val dockerImageName = "opos-image-" + id

  def createDir = {
    new File(mkRootDir).mkdir()
  }

  def deleteDir = {
    val f = new File(mkRootDir)
    if(f.exists() && f.isDirectory) f.delete() else false
  }

  def createSourceFile(source: String, extension: String) = {
    val p = mkFilePath(extension)
    val f = new File(p)
    f.createNewFile()
    val fileWriter: FileWriter = new FileWriter(p)
    val printWriter: PrintWriter = new PrintWriter(fileWriter)
    printWriter.print(source.getBytes)
    f
  }

  def createDataFile(fileName: String, content: String) = {
    val p = mkFilePath("txt", fileName)
    val f = new File(p)
    f.createNewFile()
    val fileWriter: FileWriter = new FileWriter(p)
    val printWriter: PrintWriter = new PrintWriter(fileWriter)
    printWriter.print(content.getBytes)
    f
  }

  def compileCpp(extension: String) = {
    val compile = "g++ -o " + mkFilePath() + " " + mkFilePath(extension)
    import sys.process._
    val sb = new StringBuffer()
    val outInfo = (compile run BasicIO(false, sb, None)).exitValue
    //println(outInfo)
    sb.toString
  }

  def runCpp = {
    val exec = mkRootDir + "/test"
    //println(exec)
    import sys.process._
    val sb = new StringBuffer()
    val basicIO = BasicIO(false, sb, None)
    val outInfo2 = Process(exec,  Some(new File(mkRootDir))).run(basicIO).exitValue()
    // val outInfo = (exec run BasicIO(true, sb, None)).exitValue
    //println(sb.toString)
    sb.toString
  }

  def createDockerfile(nr: Int, lang: String) = {
    var dockerfile = "FROM opos-ubuntu\n\n"
    dockerfile += "COPY . . \n\n"
    val typeLang = if (lang == "cpp") "" else lang
    dockerfile += s"CMD ./test${typeLang} < dane${nr.toInt}.txt\n"
  }

  def runBuildDockerImage = {
    val exec = s"docker build -t $dockerImageName ."
    execCommand(exec)
  }

  def runTestProgrammInDocker = {
    val exec = s"docker run --memory=100M $dockerImageName"
    execCommand(exec)
  }

  def runAsOther = {

    val exec = "docker  " + " " + mkRootDir + "/test"
    //println(exec)
    import sys.process._
    val sb = new StringBuffer()
    val basicIO = BasicIO(false, sb, None)
    val outInfo2 = Process(exec,  Some(new File(mkRootDir))).run(basicIO).exitValue()
    // val outInfo = (exec run BasicIO(true, sb, None)).exitValue
    //println(sb.toString)
    sb.toString
  }

  def readFile(fileName: String): String = {
    val path = mkFilePath(fileName = fileName)
    //println(path.toFile.getPath)
    val f = new File(path)
    if (f.exists()){
      scala.io.Source.fromFile(path).getLines().mkString("/n/r")
    } else ""
  }

  def mkRootDir = ControlDir.mainDir + id

  def mkFilePath(extension: String = "", fileName: String = "test") = {
    val r = mkRootDir + "/" + fileName
    if (extension.isEmpty) r else r + "." + extension
  }

    def execCommand(command: String) = {
    import sys.process._
    val sb = new StringBuffer()
    val basicIO = BasicIO(false, sb, None)
    val outInfo2 = Process(command, Some(new File(mkRootDir))).run(basicIO).exitValue()
    // val outInfo = (exec run BasicIO(true, sb, None)).exitValue
    //println(sb.toString)
    sb.toString
  }

}