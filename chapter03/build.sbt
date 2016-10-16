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
  settings(
    libraryDependencies += "org.specs2" %% "specs2" % "3.7" % "test"
  )
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

lazy val analytics = (
  PragmaticDemoProject("analytics")
  dependsOn(common)
  settings()
)

lazy val website = (
  PragmaticDemoProject("website")
  dependsOn(common)
  settings()
)




