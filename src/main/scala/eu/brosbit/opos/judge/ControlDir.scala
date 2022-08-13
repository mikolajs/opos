package eu.brosbit.opos.judge

import java.io.File
import eu.brosbit.opos.lib.ConfigLoader
import java.io.FileWriter
import java.io.PrintWriter

case class CodeOutput(code:Int, result:String)

/*
  Temporary it serve for one data for test in expected list!
 */
class ControlDir(id: String, source:String, extension:String, data:String){

  val mainDir = if(ConfigLoader.judgeDir.last == '/') ConfigLoader.judgeDir  else ConfigLoader.judgeDir + "/"
  val dockerImageName = "opos-ubuntu-" + id
  val fullPath = mainDir + "test_" + id
  val timeTest = 3

  //println("CONTROLDIR RUN with Extension: " + extension)
  //println(source)

  def run: CodeOutput = {
    deleteDir
    val info = runHelper
    deleteDir
    info
  }

  private def runHelper:CodeOutput = {
    if(!createDir) return CodeOutput(4, "Error: cannot create dir, connect with administrator")
    val sourceFile = createFile("test."+extension, source)
    if(!sourceFile.exists()) return CodeOutput(4, "Error: cannot create source file, connect with administrator")
    val dataFile = createFile("dane.txt", data)
    if(!dataFile.exists()) return CodeOutput(4, "Error: cannot create source file, connection with administrator")
    if(extension == "cpp") {
      //println("COMPILE")
      val compileInfo = compileCpp(extension)
       println("COMPILE INFO: " + compileInfo)
      if(compileInfo._2 != 0) return CodeOutput(3, "Error: compilation error source file, " + compileInfo)
    }
    val dockerFile = createDockerfile(1)
    if(!dockerFile.exists()) return  CodeOutput(4, "Error: cannot create docker file")
    val imageInfo = runBuildDockerImage //Errors??
    if(!imageInfo.toLowerCase().contains("successfully")) return CodeOutput(4, "Error: cannot build docker image")

    val output = runTestProgramInDocker
    runDeleteDockerImage
    CodeOutput(0, output)
  }

  private def createDir: Boolean = {
    val f = new File(fullPath)
    f.mkdir()
  }

  private def deleteDir:Boolean = {
    val f = new File(fullPath)
    if(f.isDirectory) {
      f.list().foreach(fileName => {
        val file = new File(fullPath + "/" + fileName)
        if(file.exists()) file.delete()
      })
    }
    if(f.exists() && f.isDirectory) f.delete() else false
  }

  private def createFile(fileName: String, content: String) = {
    val p = mkFilePath(fileName)
    val f = new File(p)
    f.createNewFile()
    val fileWriter: FileWriter = new FileWriter(p)
    val printWriter: PrintWriter = new PrintWriter(fileWriter)
    printWriter.write(content)
    printWriter.close()
    fileWriter.close()
    f
  }

  def compileCpp(extension: String) = {
    val compile = "g++ -o " + mkFilePath("test") + " " + mkFilePath("test."+extension)
    import sys.process._
    val sb = new StringBuffer()
    val exitVal = (compile run BasicIO(false, sb, None)).exitValue()
    //println("COMPILE COMMAND: " + compile)
    (sb.toString, exitVal)
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
  private def createDockerfile(nr:Int) = {
    var dockerfile = "FROM opos-ubuntu-jammy\n\n"
    dockerfile += "COPY . . \n\n"
    dockerfile += s"CMD timeout --signal=SIGKILL $timeTest "
    if (extension == "cpp") dockerfile += "./test"
    else if(extension == "py") dockerfile += "python3 test.py"
    else if(extension == "js") dockerfile += "node test.js"
    val p = mkFilePath("Dockerfile")
    val f = new File(p)
    f.createNewFile()
    val fileWriter: FileWriter = new FileWriter(p)
    val printWriter: PrintWriter = new PrintWriter(fileWriter)
    printWriter.write(dockerfile)
    printWriter.close()
    fileWriter.close()
    f
  }

  private def runBuildDockerImage = {
    val exec = s"docker build $fullPath -t $dockerImageName"
    execCommand(exec)
  }

  private def runTestProgramInDocker = {
    val exec = s"docker run --memory=100M $dockerImageName"
    execCommand(exec)
  }

  private def runDeleteDockerImage = {
    val exec = s"docker image rm -f $dockerImageName"
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

  private def mkFilePath(fileName:String) = fullPath + "/" + fileName

  private def execCommand(command: String) = {
    import sys.process._
    val sb = new StringBuffer()
    val basicIO = BasicIO(false, sb, None)
    val outInfo2 = Process(command, Some(new File(fullPath))).run(basicIO).exitValue()
    // val outInfo = (exec run BasicIO(true, sb, None)).exitValue
    //println( "SB: " + sb.toString)
    //println("Out " + outInfo2.toString)
    sb.toString
  }

}