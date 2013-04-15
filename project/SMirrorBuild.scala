import sbt._
import Keys._

object SMirrorBuild extends Build {

	/* Dependencies */
	val scalaTest = "org.scalatest" %% "scalatest" % "2.0.M5b" % "test"

	val scalaReflect = "org.scala-lang" % "scala-reflect" % "2.10.0"

	lazy val sMirror =
		Project(
			id = "sMirror",
			base = file("."),
			settings = Defaults.defaultSettings ++ Seq(
				libraryDependencies ++=
					Seq(scalaTest, scalaReflect),
				// publishTo := Some(Resolver.file("file",  new File(Path.userHome.absolutePath+"/.m2/repository"))), 
				// publishTo := Option(Resolver.ssh("fwbrasil.net repo", "fwbrasil.net", 8080) as ("maven") withPermissions ("0644")),
				publishTo <<= version { v: String =>
				  val nexus = "https://oss.sonatype.org/"
				  val fwbrasil = "http://fwbrasil.net/maven/"
				  if (v.trim.endsWith("SNAPSHOT")) 
				    Option(Resolver.ssh("fwbrasil.net repo", "fwbrasil.net", 8080) as("maven") withPermissions("0644"))
				  else                             
				    Some("releases" at nexus + "service/local/staging/deploy/maven2")
				},
				credentials += Credentials(Path.userHome / ".sbt" / "sonatype.credentials"),
				publishMavenStyle := true,
				publishArtifact in Test := false,
				pomIncludeRepository := { x => false },
				pomExtra := (
				  <url>http://github.com/fwbrasil/smirror/</url>
				  <licenses>
				    <license>
				      <name>LGPL</name>
				      <url>https://github.com/fwbrasil/smirror/blob/master/LICENSE-LGPL</url>
				      <distribution>repo</distribution>
				    </license>
				  </licenses>
				  <scm>
				    <url>git@github.com:fwbrasil/smirror.git</url>
				    <connection>scm:git:git@github.com:fwbrasil/smirror.git</connection>
				  </scm>
				  <developers>
				    <developer>
				      <id>fwbrasil</id>
				      <name>Flavio W. Brasil</name>
				      <url>http://fwbrasil.net</url>
				    </developer>
				  </developers>
				),
				organization := "net.fwbrasil",
				scalaVersion := "2.10.0",
				version := "0.5-SNAPSHOT"
			)
		)

}