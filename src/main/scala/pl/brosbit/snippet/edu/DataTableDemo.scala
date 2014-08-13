package pl.brosbit.snippet

import _root_.scala.xml.{NodeSeq, Text}
import _root_.net.liftweb.http.SHtml._
import _root_.net.liftweb.util.Helpers._
import  pl.brosbit.lib.DataTable
import pl.brosbit.lib.DataTableOption._

class DataTableDemo {
  
 val col = List("id","Drób","Ilość", "Kolor")
 val data = List(List("1", "Kura", "4", "zielona"),List("2", "Kaczka", "8", "czarna"), List("3","Indyk", "6", "rudy"))
  
  def demo1(xhtml: NodeSeq) :NodeSeq = {
    DataTable("#myTable")
  }
  
  def demo2(xhtml: NodeSeq) :NodeSeq = {
    DataTable("#myTable2",
            LanguageOption("pl"),  
            ExtraOptions(Map("sPaginationType" -> "two_button")),
            DataOption(col, data),
            SortingOption(Map( 1 -> Sorting.DSC)), 
            DisplayLengthOption(100, List(10,60, 100)),
           ColumnNotSearchAndHidden(List(0,3), List(0))
            )
  }
  
  def demo3(xhtml: NodeSeq) :NodeSeq = {
    DataTable("#myTable3",
            LanguageOption("pl"),  
            ExtraOptions(Map("sPaginationType" -> "two_button")),
            DataOption(col,Nil),
            SortingOption(Map( 1 -> Sorting.DSC)), 
            DisplayLengthOption(100, List(10,60, 100)),
           ColumnNotSearchAndHidden(List(0,3), List(0))
            )
  }
  
  
   
   def renderLinkAndScript(html:NodeSeq) = DataTable.mergeSources(html)
 
}
