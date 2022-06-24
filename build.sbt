ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

lazy val root = (project in file("."))
  .settings(
    name := "akka-grpc-example",
    libraryDependencies ++= Seq(
      "ch.megard" %% "akka-http-cors" % "1.1.3"
    )
  )

enablePlugins(AkkaGrpcPlugin)