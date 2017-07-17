package pl.edu.osp.lib

import java.util.GregorianCalendar

/**
  * Created by ms on 10.07.17.
  */
object Validator {

  def isEmail(email:String) = {
    val arr = email.split('@')
    if(arr.length != 2 && arr(0).length < 1 && arr(1).length < 1) false
    else true
  }

  def isPesel(pesel:String) = {
    if(pesel.trim.length == 11)
    pesel.trim.forall(ch => {
      val code = ch.toInt
      (code > 47 && code < 58)
    })
    else false
  }

  def validatePesel(pesel:String) = {
    val p = pesel.trim
    if(isPesel(p)){
      p.substring(2,4).toInt < 33 &&
        p.substring(2,4).toInt > 0 &&
        p.substring(4,6).toInt < 32 &&
        p.substring(4,6).toInt > 0 &&
        checksumPesel(p)
    } else false
  }

  private def checksumPesel(pesel: String) = {
    var sum = 0
    for (it <- 0 to 9) {
      it match {
        case i if i % 4 == 0 => sum += pesel(i).toString.toInt
        case i if i % 4 == 1 => sum += pesel(i).toString.toInt * 3
        case i if i % 4 == 2 => sum += pesel(i).toString.toInt * 7
        case i if i % 4 == 3 => sum += pesel(i).toString.toInt * 9
      }
    }
    sum %= 10
    sum = (10 - sum) % 10
    sum == pesel(10).toString.toInt
  }


  def fromPeselToBithDate(pesel:String) = {
    val xxiAge = if(pesel(2).toString.toInt > 1) true else false
    var year = pesel.substring(0,2).toInt
    var month = pesel.substring(2,4).toInt
    val day = pesel.substring(4,6).toInt
    if(xxiAge) {
      year += 2000
      month -= 20
    }
    else {
      year += 1900
    }
    new GregorianCalendar(year, month -1 , day).getTime
  }

}
