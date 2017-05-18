package pl.edu.osp.config


import net.liftweb.common._
import net.liftweb.http._
import net.liftweb.mapper._
import net.liftweb.mongodb._
import net.liftweb.util.ConnectionIdentifier
import net.liftweb.util.DefaultConnectionIdentifier
import net.liftweb.util._

import com.mongodb._
import pl.edu.osp.lib.{ConfigLoader => CL}

object MyMongoIdentifier extends ConnectionIdentifier {
  def jndiName = "monogdb"
}

object MongoConfig extends Factory with Loggable {
   CL.init
  // configure your MongoMetaRecords to use this. See lib/RogueMetaRecord.scala.
  val defaultId = new FactoryMaker[ConnectionIdentifier](MyMongoIdentifier) {}

  lazy val uri = new MongoClientURI(
    "mongodb://127.0.0.1:27017/" + CL.mongoDB
  )

  def init(): Unit = {
    val client = new MongoClient(uri)

    MongoDB.defineDb(
      defaultId.vend,
      client,
      uri.getDatabase
    )

    logger.info(s"MongoDB inited: ${uri.getHosts}")
    logger.debug(s"MongoDB options: ${uri.getOptions}")
  }
}
