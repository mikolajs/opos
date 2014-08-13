#!/usr/bin/env scala
!#

import java.sql._
import scala.collection.mutable._
class DBtest {
  Class.forName("org.postgresql.Driver")
  //Class.forName("org.sqlite.JDBC")
  //val usr = "testuser"
  //val pwd = "qwerty"
  //val url = "test1" //url może trzeba dodać hosta? ms?

  
  val usr = "vregister"
  val pwd = "qwerty"
  val url = "jdbc:postgresql:vregister"
  var timeStart = System.currentTimeMillis
  //ustanowienie połączenia
  var conn:Connection = _

  def startTimer() { timeStart = System.currentTimeMillis }
  def showTimer() =  System.currentTimeMillis - timeStart

  def initDB() {
    try {
      conn =  DriverManager.getConnection(url,usr,pwd)
    }
    catch {
      case e:Exception => println("Error: " + e.toString)
        case _ => println("Nieznany błąd połączenia z bazą")
    }

  }

  def dbClose() {
    try {
      conn.close
    }
    catch {
      case e:Exception => println("Error: " + e.toString)
        case _ => println("Nieznany błąd zamykania bazy")
    }
  }

  def readData() {
    try {
      val time1 = System.currentTimeMillis
      val metaData = conn.getMetaData
      val stat = conn.createStatement
      val result = stat.executeQuery("SELECT * FROM teachers WHERE sureName > 'Naz853000'")
      val rMetaD = result.getMetaData
      val numberOfColumn = rMetaD.getColumnCount
      val time2 = System.currentTimeMillis -time1
      val timeStr = (time2 / 1000.0).toString
      var size = 0
      while(result.next) {
        size += 1
        for(i <- (1 to numberOfColumn)) print(result.getString(i) + "|")
          println("")
      }

      println("Czas wykonania wyszukiwania: " + timeStr )
      println("Ilość rzędów: " + size)
    }
    catch {
       case e:Exception => println("Error: " + e.toString)
        case _ => println("Nieznany błąd odczytu danych")
    }
  }

  def insertData(id:Int,nazwa:String) {
    //try {
      val stat = conn.createStatement
      stat.executeUpdate("INSERT INTO test VALUES(" + id + ",'" + nazwa +  "')")
    //}
    //catch {
      //case e:Exception => println("Error: " + e.toString)
      //case _ => println("Nieznany błąd zapisu danych")
     //}
    }

  def executeQ(s:String) {
    val stat = conn.createStatement
    stat.executeUpdate(s)
  }

  def testMakeStrings():String = {
    var l:List[String] = List()
    for( i <- (10 to 100000 )) {
     l = ("INSERT INTO test VALUES(" + i.toString + ",'nazwa" + i.toString + "')")::l
    }
    l.mkString(";")
  }
  
}

val dbt = new DBtest()
println("POCZĄTEK")
dbt.initDB
var im = ""
var na = ""
//dbt.startTimer
/*for (i <- (100000  to 200000)) {
  na = "Naz" + i.toString
  dbt.insertData(i,na)
}*/

//dbt.executeQ(dbt.testMakeStrings)

//dbt.insertData2
//dbt.readData
//println("Czas zapytania: " + dbt.showTimer.toString)
dbt.dbClose
//println("KONIEC")

