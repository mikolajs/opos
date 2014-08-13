
package pl.brosbit.lib

import _root_.net.liftweb.http.ResourceServer
import _root_.scala.xml.NodeSeq
import _root_.net.liftweb.http.{ LiftRules }
import _root_.net.liftweb.http.js._
import JsCmds._
import JE._
import jquery.JqJE._
//import org.specs2.internal.scalaz.effect.Resource

// Allows the user to type stuff like Sorting.DSC to specify that a column should be sorted 
// decening instead of just a plain integer that the js api expects. 


object DataTableOption {
    /*
    * Add data to render by script - replace data in html table
    * headers length must be the same data inner list length
    */
    trait BaseOption {
        val emptyJsObj = new JsObj {
            def props = Nil
        }
        def toJsObj:JsObj
    }
    
    object Sorting extends Enumeration {
    type Sorting = Value
    val ASC = Value("asc")
    val DSC = Value("desc")
}
    
    case class DataOption(headers: List[String], data: List[List[String]]) extends BaseOption {
        override def toJsObj = {
            if (headers.length == 0) emptyJsObj
            else {
                val colArr = JsArray(headers.map(h => JsObj("sTitle" -> Str(h))))
                if(data.length == 0) {
                    	 JsObj("aoColumns" -> colArr)
                }
                else {
                     val dataArr = JsArray(data.map(d => JsArray(d.map(Str(_)): _*)): _*)
                    		 JsObj("aoColumns" -> colArr, "aaData" -> dataArr)
                }              
            }
        }

    }
    /*
    * CommboBox menu in widget. Choice for visible length of table showed at once.
    * @param: len -  default size of table
    * @param: dispSizes - list of table sizes available to choice
    */
    case class DisplayLengthOption(len: Int, dispSizes: List[Int]) extends BaseOption {

        override def toJsObj = {
            val array = JsArray(dispSizes.map(Num(_)): _*)
            JsObj("iDisplayLength" -> len, "aLengthMenu" -> array)
        }
    }

    /*
     *  Options for datatable - look at  http://datatables.net/examples/
     * For example:
     * "sPaginationType": "two_button",
 *             "bFilter": true,
  *            "bLengthChange": true,
 */
    case class ExtraOptions(option: Map[String, String]) extends BaseOption {
        def toJsObj = {
            val obj = option.map(o => JsObj(o._1 -> o._2)).toSeq
            obj.foldLeft[JsObj](emptyJsObj)(_ +* _)
        }
    }
    /*
     * internationalization for datatable
     */
    case class LanguageOption(lang: String = "") extends BaseOption {
        def toJsObj = {
            if (lang == "") emptyJsObj
            else {
                val langFileName =  "/" + LiftRules.resourceServerPath + "/datatable/language/" + lang + ".lang"
                JsObj("oLanguage" -> JsObj("sUrl" -> langFileName))
            }

        }
    }
    
    case class ColumnNotSearchAndHidden(notsearchable:List[Int], hidden:List[Int]) extends BaseOption {
        def toJsObj = {
            val sSeq = notsearchable.map(Num(_)).toSeq
            val hSeq = hidden.map(Num(_)).toSeq 
            val search = JsObj("bSearchable" -> JsFalse, "aTargets" -> JsArray(sSeq: _*)) 
            val hidde = JsObj("bVisible" -> JsFalse, "aTargets" -> JsArray(hSeq: _*))
            JsObj("aoColumnDefs" -> JsArray(search, hidde))
        }
    }
  
/*
 * Sorting options 
 */
    case class SortingOption(sorting: Map[Int, Sorting.Sorting])  extends BaseOption {
 
        def toJsObj = {
            val obj = sorting.map(o => JsArray(Num(o._1), o._2.toString)).toSeq
            JsObj("aaSorting" -> JsArray(obj: _*))
        }
    }
}

object DataTable {

    private val emptyJsObj = new JsObj {
        def props = Nil
    }

    import DataTableOption._

    
    def apply(selector:String) = renderOnLoad(selector, emptyJsObj)
    
    def apply(selector:String, options:BaseOption *) = {
        val opt = options.map(opt => opt.toJsObj).toList.reduce( (a,b) => a  +* b)
        renderOnLoad(selector, opt)
    }

    /**
     * Initializes the widget. You have to call this in boot for the widget to work.
     */
    def init() {
        ResourceServer.allow({
            case "datatable" :: tail => true
        })
    }

    def renderOnLoad(selector: String, options: JsObj): NodeSeq = {
        val onLoad = """    oTable =  jQuery('""" + selector + """').dataTable(""" + options.toJsCmd + """);  """
            Script(JsRaw(onLoad)) 
    }
    
    def  mergeSources(html:NodeSeq):NodeSeq =  <link rel="stylesheet" href={ "/" + LiftRules.resourceServerPath + "/datatable/themes/jquery.dataTables.css" } type="text/css" id="" media="print, projection, screen"/> ++
            <script type="text/javascript" src={ "/" + LiftRules.resourceServerPath + "/datatable/jquery.dataTables.js" }/> 

    /**
     * Transforms a regular table into a datatable
     */
    def jsRender(selector: String, options: JsObj): JsExp =
        JqId(selector) ~> new JsRaw("datatable(" + options.toJsCmd + ");") with JsMember

    def jsRender(selector: String): JsExp = jsRender(selector, emptyJsObj)

}

