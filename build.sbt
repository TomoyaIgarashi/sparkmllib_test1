name := "Spark MLlib examples"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.10.6"

scalacOptions ++= Seq("-Xlint", "-deprecation", "-unchecked", "-feature", "-Xelide-below", "ALL")

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "1.6.2",
  "org.apache.spark" %% "spark-mllib" % "1.6.2",
  "org.specs2" %% "specs2" % "2.4.1",
  "com.typesafe.scala-logging" %% "scala-logging-slf4j" % "2.1.2" % "compile",
  "org.slf4j" % "slf4j-api" % "1.7.12" % "compile",
  "ch.qos.logback" % "logback-classic" % "1.1.3" % "runtime"
)
