/*
 * Copyright (C) 2012   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register)
*    Apache License Version 2.0, January 2004  http://www.apache.org/licenses/
 */

package eu.brosbit.opos.lib

import java.util.{Date, Locale, GregorianCalendar}
import java.text.{SimpleDateFormat}

/** Formatowanie daty */

object Formater {
  def formatTime(t: Date): String = {
    val l = new Locale("pl")
    val sfd = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm", l)
    sfd.format(t)
  }

  def strForDateTimePicker(t: Date): String = {
    val l = new Locale("pl")
    val sfd = new SimpleDateFormat("dd.MM.yyyy, HH:mm", l)
    sfd.format(t)
  }

  def prettyDate(t: Date): String = {
    val l = new Locale("pl")
    val sfd = new SimpleDateFormat("EEE, dd MMM yyyy", l)
    sfd.format(t)
  }

  def formatDate(t: Date): String = {
    val l = new Locale("pl")
    val sfd = new SimpleDateFormat("yyyy-MM-dd", l)
    sfd.format(t)
  }

  /* @parm strDate: example 2015-08-12 10:33
   */
  def fromStringToDate(strDate: String): Date = {
    val listDate = strDate.split("-")
    if (listDate.length == 3) {
      val year :: month :: day :: rest = listDate.map(x => x.toInt).toList
      val gregorianCal = new GregorianCalendar(year, month - 1, day)
      gregorianCal.getTime
    } else
    {println("========= nieprawwidłowa data"); new Date() }

  }

  /* @parm strDate: example 12.08.2015 10:33
  *
   */
  def fromStringDataTimeToDate(strDate: String): Date = {
    val listDateTime = strDate.split(" ").map(_.trim).filter(s => (s.length > 1))
    if(listDateTime.length == 2 ) {
      val day :: month :: year :: rest1 = listDateTime.head.split('.').map(x => x.toInt).toList
      val hour :: minute :: rest2 = listDateTime.last.split(':').map(x => x.toInt).toList
      val gregorianCal = new GregorianCalendar(year, month - 1, day, hour, minute)
      gregorianCal.getTime
    } else {println("========= nieprawwidłowa data"); new Date() }
  }
}
