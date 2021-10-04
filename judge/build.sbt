val scala3Version = "3.0.2"

cancelable in Global := true

fork in run := true


lazy val root = project
  .in(file("."))
  .settings(
    name := "judge",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    libraryDependencies ++= Seq(
      "com.novocode" % "junit-interface" % "0.11" % "test",
      "io.vertx" % "vertx-core" % "4.0.2",
      "io.vertx" % "vertx-web" % "4.0.2"
    )
  )
