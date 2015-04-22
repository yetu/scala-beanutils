import bintray.Keys._
import sbt._
import sbt.Keys._

object BuildSettings {
  val buildSettings = Defaults.defaultSettings ++ Seq(
    organization := "com.yetu",
    version := "0.1.0",
    scalaVersion := "2.10.5",
    crossScalaVersions := Seq("2.10.5", "2.11.6"),
    scalacOptions += "",
    licenses := ("Apache-2.0", new java.net.URL("http://www.apache.org/licenses/LICENSE-2.0.txt")) :: Nil,
    publishMavenStyle := false,
    publishArtifact in Test := false
  )
}

object ScalaMacroDebugBuild extends Build {
  import BuildSettings._

  val macroParadise = "org.scalamacros" % "paradise" % "2.1.0-M5" cross CrossVersion.full

  lazy val root: Project = Project(
    "scala-beanutils",
    file("."),
    settings = buildSettings
  ) aggregate (macros, examples)

  lazy val macros: Project = Project(
    "macros",
    file("macros"),
    settings = buildSettings ++ Seq(
      libraryDependencies := (
        // quasiquotes are alredy added to scala-reflect starting in 2.11, but they have to be explicitly brought in for 2.10
        CrossVersion.partialVersion(scalaVersion.value) match {
          case Some((2, 10)) ⇒ libraryDependencies.value :+ "org.scalamacros" %% "quasiquotes" % "2.1.0-M5"
          case _             ⇒ libraryDependencies.value
        }
      ),
      libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value,
      resolvers += Resolver.sonatypeRepo("releases"),
      addCompilerPlugin(macroParadise)
    )
  )

  lazy val examples: Project = Project(
    "examples",
    file("examples"),
    settings = buildSettings ++ Seq(
      addCompilerPlugin(macroParadise),
      libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.4" % "test"
    )
  ) dependsOn (macros)
}
