package pl.brosbit.lib

import java.io.File
import scala.io.Source

object ConfigLoader {
  var sqlPassw = ""
  var sqlDB = ""
  var mongoDB = ""
  var emailAddr= ""
  var emailPassw = ""
  var emailPort = ""
  var emailSMTP = ""
  val f = new File("/etc/osp/config.cfg")
  val lines = Source.fromFile(f).getLines().toList

  def init = lines.map(line => {
    val opt = line.split('=').map(x => x.trim)
    if (opt.length == 2) {
      opt.head match {
        case "sqlpassword" => sqlPassw = opt.last
        case "sqldatabase" => sqlDB = opt.last
        case "mongodatabase" => mongoDB = opt.last
        case "emailaddress" => emailAddr = opt.last
        case "emailpassword" => emailPassw = opt.last
        case "emailport" => emailPort = opt.last
        case "emailsmtp" => emailSMTP  = opt.last
        case _ =>
      }
    }
  })

  def printInfo = "sqlPass: %s, sqlDB: %s, mongoDB: %s".format(sqlPassw, sqlDB, mongoDB)

}
