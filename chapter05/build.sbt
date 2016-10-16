name := "pragmatic-demo"

scalaVersion in ThisBuild := "2.11.8"

version in ThisBuild := "1.0"

organization in ThisBuild := "com.pragmaticdemo"

// Custom keys for this build.

val gitHeadCommitSha = taskKey[String]("Determines the current git commit SHA")

val makeVersionProperties = taskKey[Seq[File]]("Creates a version.properties file we can find at runtime.")


// Common settings/definitions for the build

def PragmaticDemoProject(name: String): Project = (
  Project(name, file(name))
  .settings( Defaults.itSettings : _*)
  .settings(
    libraryDependencies += "org.specs2" %% "specs2" % "3.7" % "test",
    javacOptions in Compile ++= Seq("-target", "1.6", "-source", "1.6"),
    resolvers ++= Seq(
      "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
      "teamon.eu Repo" at "http://repo.teamon.eu/"
    )
  )
  .configs(IntegrationTest)
)

gitHeadCommitSha in ThisBuild := Process("git rev-parse HEAD").lines.head


// Projects in this build

lazy val common = (
  PragmaticDemoProject("common")
  settings(
    makeVersionProperties := {
      val propFile = (resourceManaged in Compile).value / "version.properties"
      val content = "version=%s" format (gitHeadCommitSha.value)
      IO.write(propFile, content)
      Seq(propFile)
    },
    resourceGenerators in Compile <+= makeVersionProperties
  )
)

val analytics = (
  PragmaticDemoProject("analytics")
  dependsOn(common)
  settings()
)

val website = (
  PragmaticDemoProject("website")
  dependsOn(common, analytics)
  settings()
)
