package eu.brosbit.opos.judge


object TestValidator {

  val info:Map[Int, String] = Map(
    0 -> "Prawidłowy wynik",
    1 -> "Błędny wynik",
    2 -> "Błąd uruchomienia",
    3 -> "Błąd kompilacji",
    4 -> "Błąd systemu"
  )

  def validate(result:String, pattern:String) = {
    if(result.take(5) == "Error") false
    else {
      val r = result.split('\n').map(_.trim).filter(!_.isEmpty)
      val p = pattern.split('\n').map(_.trim).filter(!_.isEmpty)
      println(s"VALIDATE TEST: ${r.length} ==? ${p.length}")
      if(r.length != p.length) false
      else r.zip(p).forall(x => x._1 == x._2)
    }
  }



}
