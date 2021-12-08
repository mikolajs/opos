package eu.brosbit

import java.lang.StringBuffer
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter

class CommandExecutorHelper(idWorker:Int, idTest:String):
  val mainPath = "/home/ms/Dokumenty/forJudgeDir/"+ idTest + "/"

  def make =
    val ext = getExtensionTestFile
    val maxDataTest = getDataFilesNumbs
    if(ext == "cpp") compileCpp("cpp")
    for i <- 1 to maxDataTest do
      val dFileString = if(ext == "cpp") createDockerfile(i, "")
      else createDockerfile(i, ext)
      saveFile("Dockerfile", dFileString)
      runBuildDockerImage
      val result = runTestProgrammInDocker
      //println("THE RESULT IS::::::::::::::::: " + result)
      saveFile(s"wynik$i.txt", result)



  def compileCpp(extension:String) =
    val compile = "g++ -o "+ mkFilePath() + " " + mkFilePath(extension)
    import sys.process._
    val sb = StringBuffer()
    val outInfo = (compile run BasicIO(false, sb, None)).exitValue
    //println(outInfo)
    sb.toString

  def createDockerfile(nr:Int, lang:String) =
    var dockerfile = s"FROM opos-ubuntu\n\n"
    dockerfile += "COPY . . \n\n"
    dockerfile += "CMD "
    if lang == "cpp" then dockerfile += "./test"
    else if lang == "py" then dockerfile += "python3 ./test.py"
    else if lang == "js" then dockerfile += "node ./test.js"
    dockerfile += s" < dane${nr.toInt}.txt\n"
    dockerfile

  def runBuildDockerImage =
    val exec = s"docker build -t $createDockerImageName ."
    execCommand(exec)

  def runTestProgrammInDocker =
    val exec = s"timeout --signal=SIGKILL 5 docker run --memory=100M $createDockerImageName"
    execCommand(exec)

  def saveFile(fullName:String, dataString:String) =
    val fileWriter: FileWriter = FileWriter(mainPath + fullName)
    val printWriter: PrintWriter = PrintWriter(fileWriter)
    printWriter.print(dataString)
    printWriter.close()
    fileWriter.close()

  def getExtensionTestFile:String =
    val f = File(mainPath)
    if f.isDirectory then
      f.list().filter(_.take(5) == "test.").toList match
        case h::rest => h.split('.').last
        case _ => ""
    else ""

  def execCommand(command:String) =
    import sys.process._
    val sb = StringBuffer()
    var strLog = ""
    val basicIO = BasicIO(false, sb, Some(ProcessLogger(s => strLog += s)))
    val outInfo2 = Process(command, java.io.File(mainPath)).run(basicIO).exitValue()
    // val outInfo = (exec run BasicIO(true, sb, None)).exitValue
    //println(sb.toString)
    sb.toString + strLog

  def mkFilePath (extension:String = "", fileName:String = "test") =
    val r = mainPath  + fileName
    if extension.isEmpty then r else r + "." + extension

  def getDataFilesNumbs =
    val f = File(mainPath)
    val t = f.list().filter(_.take(4) == "dane").toList
    t.map(_(4)).map(_.toString.toInt).max

  def createDockerImageName = s"opos-image-id$idWorker"