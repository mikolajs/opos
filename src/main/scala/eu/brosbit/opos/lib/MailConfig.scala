/*
 * Copyright (C) 2012   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register)
*    Apache License Version 2.0, January 2004  http://www.apache.org/licenses/
 */

package eu.brosbit.opos.lib

import javax.mail._
import javax.mail.internet._
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.eu.brosbit.opos.model.MapExtraData
import net.liftweb.util.Mailer.{PlainPlusBodyType, To}


class MailConfig {
   
   /**For configure mail inside snippet */  
   def configureMailer(host: String, port: String, user: String, password: String) {
      this.host = host
      this.user = user
      this.port = port
      this.password = password
      mkConfig()

   }  
   
   /** For configure in bootstrap if database contain data configuration*/
    def autoConfigure(){
     loadConfig()
     mkConfig()
      println("\n\n Sending Email \n\n" )
      Thread.sleep(5000)
      Mailer.sendMail(Mailer.From("20logdansk@gmail.com"), Mailer.Subject("restarted app OPOS"),
        PlainPlusBodyType("Started", "utf-8"), To("mikolajsochacki@gmail.com"))
   }
  
   def getConfig() = {
     loadConfig()
     (host, port, user,password)
   }
  
   private var host = ""
   private var user = ""
   private var password = ""
   private var port = ""
     
   private def loadConfig() = {
      host = ConfigLoader.emailSMTP
      port = ConfigLoader.emailPort
      user = ConfigLoader.emailAddr
      password = ConfigLoader.emailPassw

   }

   
  private def mkConfig(){
    println("[App Info]: mkConfig in Mailer user %s password %s host %s port %s".format(user, password, host, port))
    /*
         // Enable TLS support
      System.setProperty("mail.smtp.starttls.enable", "true");
      // Set the host name
      System.setProperty("mail.smtp.host", this.host) // Enable authentication
      System.setProperty("mail.smtp.starttls.enable", "true");
      System.setProperty("mail.smtp.port", this.port)
      println("HOST NAME ::::: " + System.getProperty("mail.smtp.host") + " PORT ::: " + System.getProperty("mail.smtp.port"))
      */
     Mailer.customProperties = Map(
          "mail.smtp.host" -> host,
          "mail.smtp.port" -> port,
          "mail.smtp.auth" -> "true",
       //"mail.smtp.ssl.trust" -> "smtp.gmail.com",
       "mail.smtp.ssl.protocols" ->"TLSv1.2",
      // "mail.smtp.socketFactory.port" -> "465",
      //"mail.smtp.socketFactory.class" -> "javax.net.ssl.SSLSocketFactory",
       //"mail.smtp.ssl.enable" -> "true",
       "mail.smtp.starttls.enable" -> "true"
     )
       // Provide a means for authentication. Pass it a Can, which can either be Full or Empty
     Mailer.authenticator = Full(new Authenticator {
       override def getPasswordAuthentication = new PasswordAuthentication( user, password)
      })
     
    }

}
