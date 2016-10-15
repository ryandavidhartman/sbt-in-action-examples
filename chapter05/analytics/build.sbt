// specs2 libraries.

libraryDependencies += "org.specs2" %% "specs2" % "1.14" % "test"

libraryDependencies += "org.pegdown" % "pegdown" % "1.0.2" % "test"
                            
testOptions += Tests.Argument(TestFrameworks.Specs2, "html")

javaOptions in Test += "-Dspecs2.outDir=" + (target.value / "generated/test-reports").getAbsolutePath

fork in Test := true

// scalacheck

libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.10.0" % "test"
                            
testOptions += Tests.Argument(TestFrameworks.ScalaCheck, "-s", "500")

// junit

libraryDependencies += "junit" % "junit" % "4.11" % "test"

libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % "test"

testOptions += Tests.Argument(TestFrameworks.JUnit, "-v", "-n", "--run-listener=com.pragmaticdemo.sbt.JUnitListener")

javaOptions in Test += "-Djunit.output.file=" + (target.value / "generated/junit.html").getAbsolutePath
