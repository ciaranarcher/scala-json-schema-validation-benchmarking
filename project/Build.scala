import sbt._
import Keys._

object Build extends Build {

  lazy val project = Project("root", file(".")).settings(

    // basics
    name := "scala-map-benchmarking",
    organization := "cat.dvmlls",
    version := "1.0.0-SNAPSHOT",
    scalaVersion := "2.11.6",
    scalacOptions ++= Seq("-deprecation", "-feature"),

    resolvers in ThisBuild ++= Seq(
      "emueller-bintray" at "http://dl.bintray.com/emueller/maven",
      "sonatypeSnapshots" at "http://oss.sonatype.org/content/repositories/snapshots"
    ),

    // dependencies
    libraryDependencies ++= Seq(
      "com.carrotsearch" % "hppc" % "0.7.1",
      "net.sf.trove4j" % "trove4j" % "3.0.3",
      "com.google.code.java-allocation-instrumenter" % "java-allocation-instrumenter" % "2.0",
      "com.google.code.gson" % "gson" % "1.7.1",
      "org.scalatest" %% "scalatest" % "2.2.1" % "test",
      "junit" % "junit" % "4.12" % "test",
      "org.scalacheck" %% "scalacheck" % "1.12.2" % "test",
      "com.eclipsesource" %% "play-json-schema-validator" % "0.8.5",
      "org.json" % "json" % "20160810",
      "org.everit.json" % "org.everit.json.schema" % "1.4.1",
      "com.github.fge" % "json-schema-validator" % "2.2.6",
      "com.networknt" % "json-schema-validator" % "0.1.3"
  ),

    // enable forking in run
    fork in run := true,

    // we need to add the runtime classpath as a "-cp" argument to the `javaOptions in run`, otherwise caliper
    // will not see the right classpath and die with a ConfigurationException
    javaOptions in run ++= Seq("-cp",
      Attributed.data((fullClasspath in Runtime).value).mkString(":"))
  )
}
