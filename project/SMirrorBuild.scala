import sbt._
import Keys._

object SMirrorBuild extends Build {

	/* Dependencies */
	val scalaTest = "org.scalatest" %% "scalatest" % "2.0.M5b" % "test"

	val scalaReflect = "org.scala-lang" % "scala-reflect" % "2.10.0"

	/* Resolvers */
	val customResolvers = Seq(
		"snapshots" at "http://scala-tools.org/repo-snapshots",
		"releases" at "http://scala-tools.org/repo-releases",
		"Maven" at "http://repo1.maven.org/maven2/"
	)

	lazy val sMirror =
		Project(
			id = "sMirror",
			base = file("."),
			settings = Defaults.defaultSettings ++ Seq(
				libraryDependencies ++=
					Seq(scalaTest, scalaReflect),
				//      publishTo := Some(Resolver.file("file",  new File(Path.userHome.absolutePath+"/.m2/repository"))), 
				publishTo := Option(Resolver.ssh("fwbrasil.net repo", "fwbrasil.net", 8080) as ("maven") withPermissions ("0644")),
				organization := "net.fwbrasil",
				scalaVersion := "2.10.0",
				version := "0.1",
				resolvers ++= customResolvers
			)
		)

}