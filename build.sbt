name := "opos"
version := "0.9.1"

organization := "brosbit.eu"

scalaVersion := "2.11.7"

offline := true

resolvers ++= Seq("snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
		  "staging" at "http://oss.sonatype.org/content/repositories/staging",
                  "releases" at "http://oss.sonatype.org/content/repositories/releases" )

unmanagedResourceDirectories in Test <+= (baseDirectory) { _ / "src/main/webapp" }

seq(webSettings :_*)

scalacOptions ++= Seq("-deprecation", "-unchecked")

libraryDependencies ++= {
  val liftVersion = "2.6.2"
  Seq(
    "net.liftweb" %% "lift-webkit" % liftVersion % "compile",
    "net.liftweb" %% "lift-mapper" % liftVersion % "compile",
    "net.liftweb" %% "lift-actor" % liftVersion % "compile",
    "net.liftmodules" % "lift-jquery-module_2.6_2.11" % "2.8",
     "org.eclipse.jetty" % "jetty-webapp" % "9.1.0.v20131115" % "container",
    "org.eclipse.jetty" % "jetty-plus"   % "9.1.0.v20131115" % "container",
    "ch.qos.logback" % "logback-classic" % "1.0.6",
    "org.jsoup" % "jsoup" % "1.8.3",
     "net.liftweb" %% "lift-mongodb" % liftVersion % "compile",
    "org.postgresql" % "postgresql" % "42.2.5",
  "com.sun.xml.messaging.saaj" % "saaj-impl" % "2.0.0"
  )

}

port in container.Configuration := 9090


