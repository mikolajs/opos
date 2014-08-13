/*
 * Copyright (C) 2011   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register - Wirtualny Dziennik)
 *
 *   VRegister is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU AFFERO GENERAL PUBLIC LICENS Version 3
 *   as published by the Free Software Foundation
 *
 *   VRegister is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU AFFERO GENERAL PUBLIC LICENS
 *   along with VRegister.  If not, see <http://www.gnu.org/licenses/>.
 */

package pl.brosbit.snippet 

    import _root_.scala.xml.{ NodeSeq, Text }
    import _root_.net.liftweb.util._
    import _root_.net.liftweb.http.{ SHtml, S }
    import _root_.net.liftweb.common._
    import _root_.java.util.{ Date, Random }
    import pl.brosbit.model.{ MarkMap }
    import _root_.net.liftweb.mapper.{ By, OrderBy, Ascending }
    import Helpers._

    class MarkMapSn {

      /*
      def markList(in: NodeSeq): NodeSeq = {

        val markList: List[MarkMap] = MarkMap.findAll(OrderBy(MarkMap.name, Ascending))
        val cont = <tbody id="list">{
          for (markObj <- markList) yield {
            <tr id={ markObj.id.is.toString }>
              <td>{ markObj.name.is }</td>
              <td>{ markObj.value.is.toString }</td>
              <td><button name="deleteMark" onclick="deleteMark(this)">Edytuj!</button></td>
            </tr> //nieprawidłowe id w cudzysłowiu
          }
        }</tbody>
        cont
      }

      /** dodanie formatki i obsługa */
      def formItem(in: NodeSeq): NodeSeq = {
        var dataStrD = ""
        var dataStrN = ""

        def processEntry() {
          //zakłądam że  przyjdzie prawidłowy string lub go nie ma
          //S.notice(dataStrEA)

          val lines1 = dataStrN.split(";")
          for (line <- lines1) {
            if (line.size > 5) {
              val data = line.split(",")
              val id = data(0).toInt
              if (id < 0) {
                val m = MarkMap.create
                m.name(data(1))
                m.value(data(2).toInt)
                m.save
              }
            }
          }

          val idToDel = dataStrD.split(';')

          for (id <- idToDel) {
            if (id.size > 0) {
              val m = MarkMap.find(id.toInt).get
              m.delete_!
            }
          }

        }
        Helpers.bind("entry", in,
          "dataN" -> SHtml.text(dataStrN, (x) => dataStrN = x, "id" -> "dataN", "type" -> "hidden"),
          "dataD" -> SHtml.text(dataStrD, (x) => dataStrD = x, "id" -> "dataD", "type" -> "hidden"),
          "submit" -> SHtml.submit("Zapisz zmiany!", processEntry, "onclick" -> "createData()"))

      } */
    }

