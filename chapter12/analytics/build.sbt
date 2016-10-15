import com.typesafe.sbt.SbtNativePackager._
import NativePackagerKeys._

//--------
// Testing
//--------

// specs2 libraries.

libraryDependencies += "org.specs2" %% "specs2" % "1.14" % "test"

libraryDependencies += "org.pegdown" % "pegdown" % "1.0.2" % "test"
                            
testOptions += Tests.Argument(TestFrameworks.Specs2, "html")

javaOptions in Test += "-Dspecs2.outDir=" + (target.value / "generated/test-reports").getAbsolutePath

// Workaround conflicting definitions in Play 2.2.x
_root_.sbt.Keys.fork in Test := true

// scalacheck

libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.10.0" % "test"
                            
testOptions += Tests.Argument(TestFrameworks.ScalaCheck, "-s", "500")

// junit

libraryDependencies += "junit" % "junit" % "4.11" % "test"

libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % "test"

testOptions += Tests.Argument(TestFrameworks.JUnit, "-v", "-n", "--run-listener=com.pragmaticdemo.sbt.JUnitListener")

javaOptions in Test += "-Djunit.output.file=" + (target.value / "generated/junit.html").getAbsolutePath


//-----------
// Packaging
//-----------

mainClass := Some("org.pragmaticdemo.Analytics")

packageArchetype.java_server

bashScriptExtraDefines += """addJava "-Danalytics.properties=${app_home}/../conf/analytics.properties""""

batScriptExtraDefines += """set _JAVA_OPTS=%_JAVA_OPTS% -Danalytics.properties=%ANALYTICS_HOME%\\conf\\analytics.properties"""

maintainer := "Josh Suereth <pet-them-all@pragmatic-demo.com>"

packageSummary := "Analytics server for prewoned-demo.com"

packageDescription := """Contains the analytics of kitten-owner compatibilities."""

debianPackageDependencies in Debian ++= Seq("java7-runtime-headless", "bash")
