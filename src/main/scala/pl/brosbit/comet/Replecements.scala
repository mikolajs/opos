package pl.brosbit.comet

import net.liftweb.actor.{ LiftActor, LAPinger }
import net.liftweb.util.Mailer
import Mailer._
import java.security.MessageDigest

class Replecements extends LiftActor {

  LAPinger.schedule(this, Check, 10000L)
  override protected def messageHandler = {
    case Check => {
      checkReplacement
      LAPinger.schedule(this, Check, 10000L)
    }
  }

  private def checkReplacement() {

    import java.io._
    import java.net._
    import scala.xml._

    var url: URL = null
    var conn: HttpURLConnection = null
    var rd: BufferedReader = null
    var line = ""
    var result = "";
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
        val urlData = new URL("http://xxlo.pl/dokumenty/" + href.get.drop(5))
        result = ""
        conn = urlData.openConnection().asInstanceOf[HttpURLConnection];
        conn.setRequestMethod("GET");
        rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        while (rd.ready()) {
          result += rd.readLine()
        }
        rd.close();
        val reg = new scala.util.matching.Regex("Mikołaj Sochacki")
        val m = reg.findFirstMatchIn(result)
        if (!m.isEmpty) {
        val md5 = MessageDigest.getInstance("MD5").digest(result.getBytes())
        val md5Sum = new String(md5)
         val body =""" Twoje nazwisko znalazło się na stronie z aktualnymi zastępstwami. """ + 
         "\n ----------\n Informacja wysłana ze strony - nie odpowiadaj na nią " + 
        	"Jeśli nie chcesz dalej otrzymywać tego typu wiadomości skontaktuj się z administratorem "
         Mailer.sendMail(From("nieodpowiadaj@xxlo.pl"), Subject("Zastępstwo"), 
          To("mikolajsochacki@gmail.com"), PlainMailBodyType(body))
          println("send Mail!!!")
        }

      } else println("EMPTY!")
    } catch {
      case e: IOException => e.printStackTrace();
      case e: Exception => e.printStackTrace();
    }
  }
}

case class Check()
