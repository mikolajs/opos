/*
 * Copyright (C) 2012   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register)
*    Apache License Version 2.0, January 2004  http://www.apache.org/licenses/
 */

package pl.brosbit.lib

import javax.mail._
import javax.mail.internet._
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.pl.brosbit.model.MapExtraData


class MailConfig {

  /** For configure mail inside snippet */
  def configureMailer(host: String, port: String, user: String, password: String) {
    this.host = host
    this.user = user
    this.port = port
    this.password = password
    saveToDB()
    mkConfig()
  }

  /** For configure in bootstrap if database contain data configuration */
  def autoConfigure() {
    loadFromDB()
    mkConfig()
  }

  def getConfig() = {
    loadFromDB
    (host, port, user, password)
  }

  private var host = ""
  private var user = ""
  private var password = ""
  private var port = ""

  private def loadFromDB() = {
    val configMap = MapExtraData.getMapData("sendmailconfig")
    configMap.keys.map(key => {
      key match {
        case "host" => host = configMap("host")
        case "port" => port = configMap("port")
        case "user" => user = configMap("user")
        case "password" => password = configMap("password")
        case _ =>
      }
    })
  }

  private def saveToDB() = {
    val data: Map[String, String] = Map(("host" -> host), ("user" -> user), ("password" -> password), ("port" -> port))
    MapExtraData.setMapData("sendmailconfig", data)
  }

  private def mkConfig() {
    println("[App Info]: mkConfig in Mailer user %s password %s host %s port %s".format(user, password, host, port))
    // Enable TLS support
    System.setProperty("mail.smtp.starttls.enable", "true");
    // Set the host name
    System.setProperty("mail.smtp.host", this.host) // Enable authentication
    //System.setProperty("mail.smtp.port", port);
    println("HOST NAME ::::: " + System.getProperty("mail.smtp.host") + " PORT ::: " + System.getProperty("mail.smtp.port"))
    System.setProperty("mail.smtp.auth", "true") // Provide a means for authentication. Pass it a Can, which can either be Full or Empty
    Mailer.authenticator = Full(new Authenticator {
      override def getPasswordAuthentication = new PasswordAuthentication(user, password)
    })

  }

}
