package eu.brosbit

import java.nio.file.*
import java.nio.charset.StandardCharsets

class ControlDir:
  val mainDir = "/home/ms/Dokumenty/dir_"
  def createFile(id:String) = Files.createDirectory(Paths.get(mainDir+id))
  def deleteFile(id:String) = 
    val p = Paths.get(mainDir+id)
    val l = Files.list(p)
    l.forEach(file => Files.delete(file))
    Files.delete(p)

  def createFile(id:String, source:String, extension:String) = 
    val p = Paths.get(mainDir+id+"/test."+extension)
    Files.createFile(p)
    val file = Files.write(p, source.getBytes)
    file

  def compileCpp(id:String, extension:String) = 
    val op = mainDir+id+"/test."+extension
    val compile = "g++ -o "+mainDir+id+ "/test " + op
    import sys.process._
    val sb = StringBuffer()
    val outInfo = (compile run BasicIO(false, sb, None)).exitValue
    println(outInfo)
    sb.toString

  def runCpp(id:String) = 
    val exec = mainDir+id+"/test"
    import sys.process._
    val sb = StringBuffer()
    val outInfo = (exec run BasicIO(false, sb, None)).exitValue
    println(outInfo)
    sb.toString
     
  
  def readFile(id:String, fileName:String) = 
    val path = Paths.get(mainDir+id+"/" + fileName)
    if Files.exists(path) then
      val ar = Files.readAllBytes(path).split('\n').map(_.trim)
       String(ar, StandardCharsets.UTF_8)
    else 
      ""




