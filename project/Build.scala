import sbt._
import sbt.Keys._

object Build extends Build {

  val root = Project("root", file("."))
    .settings(
      name                := "jabba",
      organization        := "com.plasmaconduit",
      version             := "0.1.0",
      scalaVersion        := "2.10.0",
      licenses            += ("MIT", url("http://opensource.org/licenses/MIT")),
      scalacOptions       += "-feature",
      scalacOptions       += "-deprecation",
      //libraryDependencies += "org.scala-lang" %% "scala-pickling" % "0.8.0",
      libraryDependencies += "com.netflix.rxjava" % "rxjava-scala" % "0.20.3",
      libraryDependencies += "org.seleniumhq.selenium" % "selenium-java" % "2.42.2",
      libraryDependencies ++= Seq(
        "org.apache.kafka" % "kafka_2.10" % "0.8.1.1"
        exclude("javax.jms", "jms")
        exclude("com.sun.jdmk", "jmxtools")
        exclude("com.sun.jmx", "jmxri")
      )
    )

}