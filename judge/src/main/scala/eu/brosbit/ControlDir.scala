package eu.brosbit

import java.nio.charset
import java.nio.file.*
import java.nio.charset.StandardCharsets

object ControlDir:
  val mainDir = "/home/ms/Dokumenty/forJudgeDir/dir_"

class ControlDir(id:String):
  val dockerImageName = "opos-image-" + id
  def createDir = Files.createDirectory(mkPathObject(mkRootDir))
  def deleteDir = 
    val p = mkPathObject(mkRootDir)
    val l = Files.list(p)
    l.forEach(file => Files.delete(file))
    Files.delete(p)

  def createSourceFile(source:String, extension:String) =
    val p = mkPathObject(mkFilePath(extension))
    Files.createFile(p)
    val file = Files.write(p, source.getBytes)
    file

  def createDataFile(fileName:String, content:String) =
    val p = mkPathObject(mkFilePath("txt", fileName))
    Files.createFile(p)
    val file = Files.write(p, content.getBytes)
    file

  def compileCpp(extension:String) = 
    val compile = "g++ -o "+ mkFilePath() + " " + mkFilePath(extension)
    import sys.process._
    val sb = StringBuffer()
    val outInfo = (compile run BasicIO(false, sb, None)).exitValue
    //println(outInfo)
    sb.toString

  def runCpp = 
    val exec = mkRootDir + "/test"
    //println(exec)
    import sys.process._
    val sb = StringBuffer()
    val basicIO = BasicIO(false, sb, None)
    val outInfo2 = Process(exec, java.io.File(mkRootDir)).run(basicIO).exitValue()
   // val outInfo = (exec run BasicIO(true, sb, None)).exitValue
    //println(sb.toString)
    sb.toString

  def createDockerfile(nr:Int, lang:String) =
    var dockerfile = "FROM opos-ubuntu\n\n"
    dockerfile += "COPY . . \n\n"
    val typeLang = if lang == "cpp" then "" else lang
    dockerfile += s"CMD ./test${typeLang} < dane${nr.toInt}.txt\n"

  def runBuildDockerImage =
    val exec = s"docker build -t $dockerImageName ."
    execCommand(exec)

  def runTestProgrammInDocker =
    val exec = s"docker run --memory=100M $dockerImageName"
    execCommand(exec)

  def runAsOther =
    val exec = "docker  " + " " + mkRootDir + "/test"
    //println(exec)
    import sys.process._
    val sb = StringBuffer()
    val basicIO = BasicIO(false, sb, None)
    val outInfo2 = Process(exec, java.io.File(mkRootDir)).run(basicIO).exitValue()
    // val outInfo = (exec run BasicIO(true, sb, None)).exitValue
    //println(sb.toString)
    sb.toString

  def readFile(fileName:String):String =
    val path = mkPathObject(mkFilePath(fileName = fileName))
    //println(path.toFile.getPath)
    if Files.exists(path) then
      val ar = String(Files.readAllBytes(path), StandardCharsets.UTF_8).split('\n').map(_.trim).mkString
      ar
    else 
      ""

  def mkRootDir = ControlDir.mainDir + id
  def mkFilePath (extension:String = "", fileName:String = "test") =
    val r = mkRootDir + "/" + fileName 
    if extension.isEmpty then r else r + "." + extension
  def mkPathObject(filePath:String) = Paths.get(filePath)
    
  def execCommand(command:String) =
    import sys.process._
    val sb = StringBuffer()
    val basicIO = BasicIO(false, sb, None)
    val outInfo2 = Process(command, java.io.File(mkRootDir)).run(basicIO).exitValue()
    // val outInfo = (exec run BasicIO(true, sb, None)).exitValue
    //println(sb.toString)
    sb.toString

