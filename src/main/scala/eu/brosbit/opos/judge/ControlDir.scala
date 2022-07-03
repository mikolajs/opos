package eu.brosbit.opos.judge

import java.nio.charset
import java.io.File

import java.nio.charset.StandardCharsets
import eu.brosbit.opos.lib.ConfigLoader
import java.io.FileWriter
import java.io.PrintWriter

object ControlDir {
  val mainDir = if(ConfigLoader.judgeDir.last == '/') ConfigLoader.judgeDir  else ConfigLoader.judgeDir + "/"
}

/*
  Temporary it serve for one data for test in expected list!
 */
class ControlDir(id: String, source:String, extension:String, expected:List[String]){

  val dockerImageName = "opos-image-" + id
  val fullPath = ControlDir.mainDir + "test_" + id
  val timeTest = 3


  def run:String = {
    createDir
    val info = runHelper
    deleteDir
    info
  }

  private def runHelper:String = {
    if(!createDir) return "Error: cannot create dir, connect with administrator"
    val sourceFile = createSourceFile()
    if(!sourceFile.exists()) return "Error: cannot create source file, connect with administrator"
    val dataFile = createDataFile("test", expected.head)
    if(!dataFile.exists()) return "Error: cannot create source file, connection with administrator"
    if(extension == "cpp") {
      val compileInfo = compileCpp(source).toLowerCase()
      if(compileInfo.contains("error")) return "Error: compilation error source file"
    }
    val dockerFile = createDockerfile(1, extension)
    if(!dockerFile.exists()) return  "Error: cannot create docker file";
    val imageInfo = runBuildDockerImage //Errors??
    if(!imageInfo.toLowerCase().contains("succesfully")) return "Error: cannot build docker image"

    val output = runTestProgramInDocker
    runDeleteDockerImage
    output
  }

  private def createDir: Boolean = new File(fullPath).mkdir()

  private def deleteDir = {
    val f = new File(fullPath)
    if(f.exists() && f.isDirectory) f.delete() else false
  }

  def createSourceFile() = {
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
    val exec = fullPath + "/test"
    //println(exec)
    import sys.process._
    val sb = new StringBuffer()
    val basicIO = BasicIO(false, sb, None)
    val outInfo2 = Process(exec,  Some(new File(fullPath))).run(basicIO).exitValue()
    // val outInfo = (exec run BasicIO(true, sb, None)).exitValue
    //println(sb.toString)
    sb.toString
  }

  //ONLY ONE DATA FILE?
  private def createDockerfile(nr: Int, lang: String) = {
    var dockerfile = "FROM opos-ubuntu\n\n"
    dockerfile += "COPY . . \n\n"
    val typeLang = if (lang == "cpp") "" else lang
    dockerfile += s"CMD timeout --signal=SIGKILL $timeTest ./test${typeLang} < dane${nr}.txt"
    val p = mkFilePath("", "Dockerfile")
    val f = new File(p)
    f.createNewFile()
    val fileWriter: FileWriter = new FileWriter(p)
    val printWriter: PrintWriter = new PrintWriter(fileWriter)
    printWriter.print(dockerfile.getBytes)
    f
  }

  private def runBuildDockerImage = {
    val exec = s"docker build $fullPath -t $dockerImageName "
    execCommand(exec)
  }

  private def runTestProgramInDocker = {
    val exec = s"docker run --memory=100M $dockerImageName"
    execCommand(exec)
  }

  private def runDeleteDockerImage = {
    val exec = s"docker image rm $dockerImageName"
    execCommand(exec)
  }

  def runAsOther = {

    val exec = "docker  " + " " + fullPath + "/test"
    //println(exec)
    import sys.process._
    val sb = new StringBuffer()
    val basicIO = BasicIO(false, sb, None)
    val outInfo2 = Process(exec,  Some(new File(fullPath))).run(basicIO).exitValue()
    // val outInfo = (exec run BasicIO(true, sb, None)).exitValue
    //println(sb.toString)
    sb.toString
  }

  private def readFile(fileName: String): String = {
    val path = mkFilePath(fileName = fileName)
    //println(path.toFile.getPath)
    val f = new File(path)
    if (f.exists()){
      scala.io.Source.fromFile(path).getLines().mkString("/n/r")
    } else ""
  }

  private def mkFilePath(extension: String = "", fileName: String = "test") = {
    val r = fullPath + "/" + fileName
    if (extension.isEmpty) r else r + "." + extension
  }

  private def execCommand(command: String) = {
    import sys.process._
    val sb = new StringBuffer()
    val basicIO = BasicIO(false, sb, None)
    val outInfo2 = Process(command, Some(new File(fullPath))).run(basicIO).exitValue()
    // val outInfo = (exec run BasicIO(true, sb, None)).exitValue
    //println(sb.toString)
    sb.toString
  }

}