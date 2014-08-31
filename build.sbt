name := "osp"

version := "0.3.1"

organization := "pl.brosbit"

scalaVersion := "2.10.4"

resolvers ++= Seq("snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
		  "staging" at "http://oss.sonatype.org/content/repositories/staging",
                  "releases" at "http://oss.sonatype.org/content/repositories/releases",
"Google GData" at "http://mandubian-mvn.googlecode.com/svn/trunk/mandubian-mvn/repository"
                 )

seq(webSettings :_*)

unmanagedResourceDirectories in Test <+= (baseDirectory) { _ / "src/main/webapp" }


scalacOptions ++= Seq("-deprecation", "-unchecked")

libraryDependencies ++= {
  val liftVersion = "2.5.1"
  val gdataVersion = "1.41.1"
  Seq(
    "net.liftweb" %% "lift-webkit" % liftVersion % "compile",
    "net.liftweb" %% "lift-mapper" % liftVersion % "compile",
    "net.liftmodules" %% "lift-jquery-module_2.5" % "2.3",
     "org.eclipse.jetty" % "jetty-webapp" % "9.1.0.v20131115" % "container",
    "org.eclipse.jetty" % "jetty-plus"   % "9.1.0.v20131115" % "container",
    "ch.qos.logback" % "logback-classic" % "1.0.6",
     "net.liftweb" %% "lift-mongodb" % liftVersion % "compile",	    	
    "postgresql" % "postgresql" % "9.1-901.jdbc4" % "compile", 
    "org.specs2" %% "specs2" % "1.14" % "test",
"com.google.gdata" % "gdata-core-1.0" % gdataVersion % "compile",
"com.google.gdata" % "gdata-base-1.0" % gdataVersion % "compile",
"com.google.gdata" % "gdata-photos-2.0" % gdataVersion % "compile",
"com.google.gdata" % "gdata-client-1.0" % gdataVersion % "compile",
"com.google.gdata" % "gdata-media-1.0" % gdataVersion % "compile",
"com.google.gdata" % "gdata-client-meta-1.0" % gdataVersion % "compile",
"com.google.gdata" % "gdata-photos-meta-2.0" % gdataVersion % "compile"  )
}


