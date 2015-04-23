

val gitVersionIdentifier = settingKey[String]("current git tag name or commit SHA")

gitVersionIdentifier in ThisBuild := {
  val output = Option(System.getProperty("git.tag"))
  output getOrElse Process("git rev-parse HEAD").lines.head
}
