package pl.brosbit.snippet.secretariat

import scala.collection.mutable._
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.pl.brosbit.model.page._
import _root_.pl.brosbit.model._
import _root_.net.liftweb.http.{ S, SHtml}
import Helpers._
import _root_.net.liftweb.json.JsonDSL._

class BellsSn {
    val beginArray:ArrayBuffer[String] = ArrayBuffer("", "", "", "", "", "", "", "", "", "", "")
    val endArray:ArrayBuffer[String] = ArrayBuffer("", "", "", "", "", "", "", "", "", "", "")
  
  def editBells() = {
    
    insertTimeToArray()
    def save(){
      println(beginArray)
      println(endArray)
      val bellsData = BellsData.findAll match {
        case bellsData::list => bellsData
        case _ => BellsData.create
      }
      bellsData.beginLesson = beginArray.toList
      bellsData.endLesson = endArray.toList
      bellsData.save
      S.redirectTo("/secretariat/bells")
    }
    
    "#bells" #>  (0 to 10).toList.map(i => {
      <tr><td>{i.toString}</td>
      		<td>{SHtml.text(beginArray(i), x => beginArray(i) = x)}</td>
      		<td>{SHtml.text(endArray(i), x => endArray(i) = x)}</td>
      </tr> }) & 
      "#save" #>  SHtml.submit("ZAPISZ DZWONKI!", save)
      
  }
     
   private def insertTimeToArray() { 
    BellsData.findAll match {
      case bellsData::list => {
        for(i <- 0 to 10){
         beginArray(i) = bellsData.beginLesson(i)
         endArray(i) = bellsData.endLesson(i)
        }
        if(!list.isEmpty) list.head.delete
      }
      case _ =>   
    }  
   }
    	

}