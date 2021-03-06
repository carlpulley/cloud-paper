// Copyright (C) 2013  Carl Pulley
// 
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
// 
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with this program. If not, see <http://www.gnu.org/licenses/>.

import sbt._
import Process._
import Keys._
import akka.sbt.AkkaKernelPlugin
import akka.sbt.AkkaKernelPlugin.{ Dist, outputDirectory, distJvmOptions }

trait Resolvers {
  val HuddersfieldResolvers = Seq(
    "Java.net" at "http://download.java.net/maven/2/",
    "Maven Central" at "http://repo1.maven.org/",
    "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
    "Typesafe Snapshots" at "http://repo.typesafe.com/typesafe/snapshots/",
    "Sonatype Releases" at "http://oss.sonatype.org/content/repositories/releases",
    "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
  )
}

object V {
  val ACTIVEMQ = "5.7.0"
  val AKKA = "2.2.1"
  val APACHE = "2.1"
  val CAMEL = "2.11.1"
  val CONFIG = "1.0.0"
  val JCLOUDS = "1.6.1-incubating"
  val JUNIT = "4.11"
  val LIFT = "2.5.1"
  val LOG4J = "1.2.17"
  val RXSCALA = "0.15.0"
  val SCALA = "2.10.2"
  val SCALACHECK = "1.11.0"
  val SCALATEST = "2.0.M6"
  val SCALAZ = "7.1.0-M1"
  val SCALAZCAMEL = "0.4-SNAPSHOT"
  val SCALIKEJDBC = "[1.6,)"
  val SLF4J = "1.7.5"
}

trait Dependencies {
  val Miscellaneous = Seq(
    // Configuration
    "org.streum" %% "configrity-core" % V.CONFIG,
    "com.typesafe" % "config" % "1.0.2",
    // JDBC
    "com.github.seratch" %% "scalikejdbc" % V.SCALIKEJDBC,
    "com.github.seratch" %% "scalikejdbc-interpolation" % V.SCALIKEJDBC,
    "org.xerial" % "sqlite-jdbc" % "3.7.15-M1",
    // JSON (used to configure Chef)
    "net.liftweb" %% "lift-json" % V.LIFT,
    // Functional programming
    "org.scalaz" %% "scalaz-core" % V.SCALAZ,
    "org.scalaz" %% "scalaz-concurrent" % V.SCALAZ,
    // Microsoft document format handling
    "info.folone" %% "poi-scala" % "0.9",
    // Scala Serializer
    "org.scala-lang" %% "scala-pickling" % "0.8.0-SNAPSHOT",
    // Java source parser
    "com.google.code.javaparser" % "javaparser" % "1.0.10",
    // Apache commons lang (string escaping)
    "org.apache.commons" % "commons-lang3" % "3.1",
    // Rx Scala
    "com.netflix.rxjava" % "rxjava-scala" % V.RXSCALA
  )

  val Testing = Seq(
    "org.scalatest" % "scalatest_2.10" % V.SCALATEST % "test",
    "org.scalacheck" %% "scalacheck" % V.SCALACHECK % "test",
    // Mocking mail servers and clients
    "org.jvnet.mock-javamail" % "mock-javamail" % "1.9" % "test",
    "junit" % "junit" % V.JUNIT
  )
    
  val Logging = Seq(
    "org.slf4j" % "slf4j-log4j12" % V.SLF4J,
    "log4j" % "log4j" % V.LOG4J
  )

  val Akka = Seq(
    "com.typesafe.akka" %% "akka-kernel" % V.AKKA,
    "com.typesafe.akka" %% "akka-actor" % V.AKKA,
    "com.typesafe.akka" %% "akka-remote" % V.AKKA,
    "com.typesafe.akka" %% "akka-camel" % V.AKKA,
    "com.typesafe.akka" %% "akka-testkit" % V.AKKA % "test"
  )
  
  val ApacheCamel = Seq(
    "org.apache.camel" % "camel-core" % V.CAMEL,
    "org.apache.camel" % "camel-mail" % V.CAMEL,
    "org.apache.camel" % "camel-twitter" % V.CAMEL,
    "org.apache.camel" % "camel-fop" % V.CAMEL,
    "org.apache.camel" % "camel-printer" % V.CAMEL,
    "org.apache.camel" % "camel-ftp" % V.CAMEL,
    "org.apache.camel" % "camel-ssh" % V.CAMEL,
    "org.apache.camel" % "camel-spring" % V.CAMEL,
    "org.apache.camel" % "camel-jms" % V.CAMEL,
    "org.apache.camel" % "camel-velocity" % V.CAMEL,
    "org.apache.camel" % "camel-quartz" % V.CAMEL,
    "org.apache.camel" % "camel-scala" % V.CAMEL,
    "org.apache.camel" % "camel-exec" % V.CAMEL,
    "org.apache.camel" % "camel-test" % V.CAMEL % "test",
    "org.apache.activemq" % "activemq-core" % V.ACTIVEMQ,
    "org.apache.activemq" % "activemq-camel" % V.ACTIVEMQ,
    "com.typesafe.akka" %% "akka-camel" % V.AKKA,
    "scalaz.camel" %% "scalaz-camel-core" % V.SCALAZCAMEL,
    "scalaz.camel" %% "scalaz-camel-akka" % V.SCALAZCAMEL
  )
  
  val JClouds = Seq(
    "org.apache.jclouds.provider" % "aws-ec2" % V.JCLOUDS,
    "org.apache.jclouds.provider" % "rackspace-cloudservers-uk" % V.JCLOUDS,
    "org.apache.jclouds.api" % "chef" % V.JCLOUDS,
    "org.apache.jclouds.api" % "openstack-nova" % V.JCLOUDS,
    "org.apache.jclouds.driver" % "jclouds-sshj" % V.JCLOUDS,
    // Needed due to "issues"
    "com.google.code.findbugs" % "jsr305" % "1.3.9",
    "org.eclipse.jetty.orbit" % "javax.servlet" % "3.0.0.v201112011016" artifacts (Artifact("javax.servlet", "jar", "jar"))
  )

  val DefaultDependencies = Miscellaneous ++ Testing ++ Logging
}

trait TaskHelpers {
  lazy val feedback = InputKey[Unit]("feedback", "Run example.CommandLine and generate feedback for given Java sources")

  lazy val jconsole = TaskKey[Unit]("jconsole", "Run example.AkkaAssessment and monitor with JConsole")

  val exampleTasks = Seq(
  //  feedback := { 
  //    mainClass := Some("example.CommandLine")
  //    run.evaluate
  //  },
  //  jconsole := { 
  //    mainClass := Some("example.AkkaAssessment")
  //    run.evaluate
  //    "jconsole" ! 
  //  }
  )
}

object CloudPaperBuild extends Build with Resolvers with Dependencies with TaskHelpers {
  val jvmOptions = Seq("-Xms256M", "-Xmx1024M", "-XX:+UseParallelGC")

  lazy val CloudPaperSettings = Defaults.defaultSettings ++ AkkaKernelPlugin.distSettings ++ Seq(
    organization := "University of Huddersfield",
    version := "1.0",
    scalaVersion := V.SCALA,
    shellPrompt := { st => Project.extract(st).currentProject.id + "> " },
    autoCompilerPlugins := true,
    resolvers := HuddersfieldResolvers,
    libraryDependencies := DefaultDependencies,
    checksums := Seq("sha1", "md5"),
    scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature"),
    javacOptions ++= Seq("-Xlint:unchecked", "-Xlint:deprecation"),
    javaOptions ++= jvmOptions,
    parallelExecution in Test := false,
    scalacOptions += "-language:experimental.macros",
    libraryDependencies <+= scalaVersion { v => compilerPlugin("org.scala-lang.plugins" % "continuations" % v) },
    scalacOptions += "-P:continuations:enable",
    libraryDependencies ++= Akka ++ ApacheCamel ++ JClouds,
    distJvmOptions in Dist := jvmOptions.mkString(" "),
    outputDirectory in Dist := file("cookbook/cloud/files/default/cloud-deploy")
  )
  
  lazy val root = Project(
    id = "cloud-paper",
    base = file("."),
    settings = CloudPaperSettings
  ).aggregate(cloud, example)

  lazy val cloud = Project(
    id = "cloud",
    base = file("cloud"),
    settings = CloudPaperSettings
  )

  lazy val example = Project(
    id = "example",
    base = file("example"),
    settings = CloudPaperSettings ++ exampleTasks ++ Seq(
      libraryDependencies ++= Seq("org.scalacheck" %% "scalacheck" % V.SCALACHECK)
    )
  ).dependsOn(cloud)
}
