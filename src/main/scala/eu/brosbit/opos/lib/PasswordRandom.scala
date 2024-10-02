package eu.brosbit.opos.lib

object PasswordRandom {
   def create(n:Int = 10): String = {
     val r = new scala.util.Random()
     (1 to n).map( _ => {
        r.nextInt(62) match {
          case a if a < 10 => ('0'.toInt + a).toChar
          case a if a < 36 => ('a'.toInt + a - 10).toChar
          case a => ('A'.toInt + a - 36).toChar
          case _ => '_'
        }
     }).mkString
   }
}
