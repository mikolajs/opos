val scala3Version = "3.1.0"
val AkkaVersion = "2.6.17"

cancelable in Global := true

fork / run := true


lazy val root = project
  .in(file("."))
  .settings(
    name := "judge",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    libraryDependencies ++= Seq(
      "com.novocode" % "junit-interface" % "0.11" % "test",
      "org.scalactic" %% "scalactic" % "3.2.10",
      "org.scalatest" %% "scalatest" % "3.2.10" % "test",
      ("com.typesafe.akka"%% "akka-actor-typed" % AkkaVersion).cross(CrossVersion.for3Use2_13),
      ("com.typesafe.akka" %% "akka-stream" % AkkaVersion).cross(CrossVersion.for3Use2_13),
      ("com.typesafe.akka" %% "akka-http" % "10.2.7").cross(CrossVersion.for3Use2_13),
      "io.vertx" % "vertx-core" % "4.1.4",
      "io.vertx" % "vertx-web" % "4.1.4"
    )
  )
