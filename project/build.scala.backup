import sbt._
import Keys._
import com.github.siasia._
import PluginKeys._
import WebPlugin._
import WebappPlugin._

object LiftProjectBuild extends Build {
  override lazy val settings = super.settings ++ buildSettings
  
  lazy val buildSettings = Seq(
    organization := "net.brosbit4u",
    version      := "0.3",
    scalaVersion := "2.9.1")
  
  def yourWebSettings = webSettings ++ Seq(
    // If you are use jrebel
    scanDirectories in Compile := Nil,
    port in container.Configuration := 8080
    )
  
  lazy val liftQuickstart = Project(
    id = "vregister",
    base = file("."),
    settings = defaultSettings ++ yourWebSettings)
    
  lazy val defaultSettings = Defaults.defaultSettings ++ Seq(
    name := "vregister",
    resolvers ++= Seq(
      "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases", 
      "Java.net Maven2 Repository" at "http://download.java.net/maven/2/",
      "Google GData" at "http://mandubian-mvn.googlecode.com/svn/trunk/mandubian-mvn/repository"),
    
    libraryDependencies ++= {
	  val liftVersion = "2.4"
	  val gdataVersion = "1.41.1"
	  Seq(
	    "net.liftweb" %% "lift-webkit" % liftVersion % "compile",
            "net.liftweb" %% "lift-mapper" % liftVersion % "compile",
	    "org.eclipse.jetty" % "jetty-webapp" % "7.5.4.v20111024" % "container",
	    "ch.qos.logback" % "logback-classic" % "1.0.0" % "compile",
	    "net.liftweb" %% "lift-mongodb" % liftVersion % "compile",
	    "net.liftweb" %% "lift-mongodb-record" % liftVersion,	    
	    "postgresql" % "postgresql" % "9.1-901.jdbc4" % "compile", 
	    "org.scalatest" %% "scalatest" % "1.6.1" % "test",
	    "junit" % "junit" % "4.10" % "test",
            "com.google.gdata" % "gdata-core-1.0" % gdataVersion % "compile",
"com.google.gdata" % "gdata-base-1.0" % gdataVersion % "compile",
"com.google.gdata" % "gdata-photos-2.0" % gdataVersion % "compile",
"com.google.gdata" % "gdata-client-1.0" % gdataVersion % "compile",
"com.google.gdata" % "gdata-media-1.0" % gdataVersion % "compile",
"com.google.gdata" % "gdata-client-meta-1.0" % gdataVersion % "compile",
"com.google.gdata" % "gdata-photos-meta-2.0" % gdataVersion % "compile" 
	   
		)
	},

    // compile options
    scalacOptions ++= Seq("-encoding", "UTF-8", "-deprecation", "-unchecked"),
    javacOptions  ++= Seq("-Xlint:unchecked", "-Xlint:deprecation"),

    // show full stack traces
    testOptions in Test += Tests.Argument("-oF")
  )
}

