import _root_.bintray.Plugin._
import bintray.Keys._
import sbt.Keys._
import sbt._

object BuildSettings {
  val scalaVersions = Seq("2.11.8", "2.10.6", "2.12.1")
  val buildSettings = Defaults.defaultSettings ++ Seq(
    organization := "com.yetu",
    version := "0.1.5-SNAPSHOT",
    scalaVersion := scalaVersions.head,
    crossScalaVersions := scalaVersions,
    scalacOptions ++= Seq("-unchecked", "-deprecation", "-J-DbeanCompanion.debug=true"),
    licenses := ("Apache-2.0", new java.net.URL("http://www.apache.org/licenses/LICENSE-2.0.txt")) :: Nil,
    publishArtifact := false
  )
}

object ScalaBeanUtilsBuild extends Build {
  import BuildSettings._

  val macroParadise = compilerPlugin("org.scalamacros" % "paradise" % "2.1.0" % "compile" cross CrossVersion.full)

  lazy val root: Project = Project(
    "root",
    file("."),
    settings = buildSettings
  ) aggregate (macros, examples)

  lazy val macros: Project = Project(
    "scala-beanutils",
    file("macros"),
    settings = buildSettings ++ bintraySettings ++ Seq(
      libraryDependencies := (
        // quasiquotes are alredy added to scala-reflect starting in 2.11, but they have to be explicitly brought in for 2.10
        CrossVersion.partialVersion(scalaVersion.value) match {
          case Some((2, 10)) ⇒ libraryDependencies.value :+ "org.scalamacros" %% "quasiquotes" % "2.1.0"
          case _             ⇒ libraryDependencies.value
        }
      ),
      libraryDependencies ++= Seq(
        "org.scala-lang" % "scala-reflect" % scalaVersion.value,
        "org.typelevel" %% "macro-compat" % "1.1.1",
        "org.scala-lang" % "scala-compiler" % scalaVersion.value % "provided",
        macroParadise
      ),
      resolvers += Resolver.sonatypeRepo("releases"),
      publishMavenStyle := false,
      publishArtifact in (Compile, packageBin) := true,
      publishArtifact in (Test, packageBin) := false,
      publishArtifact in (Compile, packageDoc) := true,
      publishArtifact in (Compile, packageSrc) := true,
      //repository in bintray := "scala-beanutils",
      vcsUrl in bintray := Some("git@github.com:yetu/scala-beanutils.git"),
      bintrayOrganization in bintray := Some("yetu"),
      packageLabels in bintray := Seq("yetu")
    )
  )

  lazy val examples: Project = Project(
    "examples",
    file("examples"),
    settings = buildSettings ++ Seq(
      libraryDependencies ++= Seq(
        macroParadise,
        "org.scalatest" %% "scalatest" % "3.0.1" % "test"
      ),
      publishArtifact := false
    )
  ) dependsOn (macros)
}
