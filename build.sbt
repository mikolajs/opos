name := "osp"

version := "0.8.7"


organization := "pl.edu.osp"

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
    "postgresql" % "postgresql" % "9.1-901.jdbc4" % "compile")
}

port in container.Configuration := 9090


