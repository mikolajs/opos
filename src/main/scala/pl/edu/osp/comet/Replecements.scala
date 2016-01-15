package pl.edu.osp.comet

import net.liftweb.actor.{LiftActor, LAPinger}
import net.liftweb.util.Mailer
import Mailer._
import java.security.MessageDigest
import pl.edu.osp.model.{MapExtraData, User}
import net.liftweb.mapper.By
import pl.edu.osp.model.page.ExtraData

object Replecements {

  //pobrać z bazy danych ostatnio sprawdzany plik, jeśli pierwszy na liście nie był sprawdzany to sprawdzić.
  //i wysłać maile jeśli są nazwiska, uaktualnić nazwę pliku w bazie danych.

  def check() {

    import java.io._
    import java.net._
    import scala.xml._

    var url: URL = null
    var conn: HttpURLConnection = null
    var rd: BufferedReader = null
    var result = ""
    try {
      url = new URL("http://xxlo.pl/dokumenty/subst_left.htm")
      conn = url.openConnection().asInstanceOf[HttpURLConnection];
      conn.setRequestMethod("GET");
      rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      while (rd.ready()) {
        result += rd.readLine()
      }
      rd.close();
      val patern = "href=(.+?\\.htm)".r
      val href = patern.findFirstIn(result)
      if (!href.isEmpty) {
        println(href.get)
        val hrefLink = ExtraData.getData("zastepstwaLink")
        if (href.get != hrefLink) {
          val urlData = new URL("http://xxlo.pl/dokumenty/" + href.get.drop(5))
          result = ""
          conn = urlData.openConnection().asInstanceOf[HttpURLConnection];
          conn.setRequestMethod("GET");
          rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
          while (rd.ready()) {
            result += rd.readLine()
          }
          rd.close();
          User.findAll(By(User.role, "n")).map(user => {
            val reg = new scala.util.matching.Regex(user.getFullName)
            val m = reg.findFirstMatchIn(result)
            if (!m.isEmpty) {
              val body = """ Twoje nazwisko znalazło się na stronie z aktualnymi zastępstwami. """ +
                """http://xxlo.pl/dokumenty/index_zastepstwa.htm""" +
                "\n ----------\n Informacja wysłana ze strony edu.epodrecznik.edu.pl - nie odpowiadaj na nią " +
                "Jeśli nie chcesz dalej otrzymywać tego typu wiadomości skontaktuj się z administratorem "
              Mailer.sendMail(From("nieodpowiadaj@xxlo.pl"), Subject("Zastępstwo"),
                To(user.email.is), PlainMailBodyType(body))
              println("send Mail!!! to " + user.getFullName)
            }
          })
          ExtraData.updateKey("zastepstwaLink", href.get)
        } else println("Allready Checked!!!")
      } else println("EMPTY!")

    } catch {
      case e: IOException => e.printStackTrace();
      case e: Exception => e.printStackTrace();
    }
  }
}

