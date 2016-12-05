package pl.edu.osp.snippet.admin

import net.liftweb.common.Full

import scala.xml.{NodeSeq, Text, XML, Unparsed}
import _root_.net.liftweb.util._
import pl.edu.osp.model.page._
import _root_.net.liftweb.http.{S, SHtml}
import _root_.net.liftweb.http.js._
import Helpers._
import org.bson.types.ObjectId
import _root_.net.liftweb.json.JsonDSL._

class AdminEditMenuSn {

  def addLinks() = {
    var xmlDataStr = ""
    def add() {
      MainPageMenu.drop
      val xmlData = XML.loadString(xmlDataStr)
      (xmlData \ "links").foreach(links => {
        val nameDep = (links \ "@name").toString
        var linksList: List[Link] = Nil
        (links \ "link").foreach(link => {
          val title = (link \ "@title").toString
          val url = (link \ "@src").toString
          linksList = Link(url, title) :: linksList
        })
        val mpm = MainPageMenu.create
        mpm.name = nameDep
        mpm.links = linksList
        mpm.save
      })
      S.redirectTo("/admin/menu")
    }
    "#xmlData" #> SHtml.text(xmlDataStr, xmlDataStr = _, "style" -> "display:none;") &
      "#submit" #> SHtml.submit("ZAPISZ", add, "onclick" -> "return saveAll()")
  }

  def showLinks() = {
    val mainPageLinks = MainPageMenu.findAll

    ".divDepartment" #> mainPageLinks.map(mpm => {
      ".nameDep" #> <input value={mpm.name} type="text"/> &
        ".linkTr" #> mpm.links.map(link => {
          ".nameLink" #> <td>
            {link.title}
          </td> &
            ".urlLink" #> <td>
              {link.url}
            </td>
        })
    })
  }

  def mkLinks() = {
    var name = ""
    var what = ""
    var dep = ""
    val listWhat = List( "d" ->"Dział", "a" -> "Wszystkie działy", "p" -> "Miejsca",
      "o" -> "Własny" )
    val departs = PageDepartment.findAll.map(pd => (pd._id.toString -> pd.name))

    def mk(): Unit ={
      what match {
        case "d" => createDepartMenu(dep, name)
        case "a" => createAllDepartMenu(name)
        case "p" => createPlacesMenu(name)
        case _ => createOwnMenu(name)
      }
    }

    ".nameAuto" #> SHtml.text(name, name = _) &
    ".selectAuto" #> SHtml.select(listWhat, Full(what), what = _ ) &
    ".selectDeparts" #> SHtml.select(departs, Full(dep), dep = _) &
    "#mkAuto" #> SHtml.submitButton(mk, "value" -> "Dodaj!")
  }

  private def createDepartMenu(dep: String, name:String): Unit = {
    PageDepartment.find(dep).map(pd => {
      val mpm = MainPageMenu.create
      mpm.name = name
      val links = ArticleHead.findAll(("departmentId" -> pd._id.toString) ~ ("news"->true),
        ("prior" -> 1) ).map(ah =>
        Link( "/page/" + pd._id.toString  + "?a="  + ah._id.toString, ah.title )
      )
      mpm.links =  links :+ Link( "/page/" + pd._id.toString , "Aktualności")
      mpm.save
    })

  }
  private def createAllDepartMenu( name:String): Unit = {
    val links = PageDepartment.findAll.map(pd =>
      Link("/page/" + pd._id.toString, pd.name)
    )
    val mpm = MainPageMenu.create
    mpm.name = name
    mpm.links = links
    mpm.save
  }
  //change to statefull menu with all content of apps in /login
  private def createPlacesMenu( name:String): Unit = {
    val mpm = MainPageMenu.create
    mpm.name = name
    val links = List(Link("/login", "Aplikacje"),
                    Link("/forum", "Forum"),
                    Link("/gallery", "Galeria"))
    mpm.links = links
    mpm.save
  }
  private def createOwnMenu(name:String): Unit = {
    val mpm = MainPageMenu.create
    mpm.name = name
    mpm.save
  }

  private def getMainPageLinks = MainPageMenu.findAll match {
    case head :: list => head
    case _ => MainPageMenu.create
  }

}
