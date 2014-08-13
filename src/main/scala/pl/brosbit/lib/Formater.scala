/*
 * Copyright (C) 2012   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register)
*    Apache License Version 2.0, January 2004  http://www.apache.org/licenses/
 */

package pl.brosbit.lib

import java.util.{ Date, Locale, GregorianCalendar }
import java.text.{ SimpleDateFormat }

/** Formatowanie daty */

object Formater {
    def formatTime(t: Date): String = {
        val l = new Locale("pl")
        val sfd = new SimpleDateFormat("EEE, dd MMM yyyy, HH:mm", l)
        sfd.format(t)
    }

    def pretyDate(t: Date): String = {
        val l = new Locale("pl")
        val sfd = new SimpleDateFormat("EEE, dd MMM yyyy", l)
        sfd.format(t)
    }

    def formatDate(t: Date): String = {
        val l = new Locale("pl")
        val sfd = new SimpleDateFormat("yyyy-MM-dd", l)
        sfd.format(t)
    }
    //nie napisana!!!
    def fromStringToDate(strDate: String): Date = {
        val listDate = strDate.split("-")
        if (listDate.length == 3) {
            val year :: month :: day :: rest = listDate.map(x => x.toInt).toList
            val gregorianCal = new GregorianCalendar(year, month, day)
            gregorianCal.getTime
        } else
            new Date()

    }
}
