version := "0.10.9"

organization := "brosbit.eu"

scalaVersion := "2.11.7"

offline := true

resolvers ++= Seq(("snapshots" at "http://oss.sonatype.org/content/repositories/snapshots").withAllowInsecureProtocol(true),
		  ("staging" at "http://oss.sonatype.org/content/repositories/staging").withAllowInsecureProtocol(true),
                  ("releases" at "http://oss.sonatype.org/content/repositories/releases").withAllowInsecureProtocol(true) )

//unmanagedResourceDirectories in Test <+= (baseDirectory) { _ / "src/main/webapp" }
Test / resourceDirectory := baseDirectory.value / "src/main/webapp"

//seq(webSettings :_*)
enablePlugins(JettyPlugin)

scalacOptions ++= Seq("-deprecation", "-unchecked")

libraryDependencies ++= {
  val liftVersion = "2.6.2"
  Seq(
    "net.liftweb" %% "lift-webkit" % liftVersion % "compile",
    "net.liftweb" %% "lift-mapper" % liftVersion % "compile",
    "net.liftweb" %% "lift-actor" % liftVersion % "compile",
    "net.liftmodules" % "lift-jquery-module_2.6_2.11" % "2.8",
     //"org.eclipse.jetty" % "jetty-webapp" % "9.1.0.v20131115" % "container",
   //"org.eclipse.jetty" % "jetty-plus"   % "9.1.0.v20131115" % "container",
    "org.eclipse.jetty" % "jetty-webapp" % "9.2.30.v20200428" % "container",
   "org.eclipse.jetty" % "jetty-plus"   % "9.2.30.v20200428" % "container",
    "org.eclipse.jetty" % "jetty-runner" % "9.2.30.v20200428" % "container" intransitive(),
    "ch.qos.logback" % "logback-classic" % "1.0.6",
    "org.jsoup" % "jsoup" % "1.8.3",
     "net.liftweb" %% "lift-mongodb" % liftVersion % "compile",
    "org.postgresql" % "postgresql" % "42.2.5",
    "com.sun.xml.messaging.saaj" % "saaj-impl" % "2.0.1"
  )

}

//Container / containerLaunchCmd  :=
// { (port, path) => Seq("runner.Run", port.toString, path) }
Container / containerMain := "org.eclipse.jetty.runner.Runner"

containerPort := 9090
