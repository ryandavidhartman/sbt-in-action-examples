import org.pragmaticdemo.sbt.ScalastylePlugin._

version := "1.0"

organization := "org.pragmaticdemo"

name := "sbt-test"

org.pragmaticdemo.sbt.HelloPlugin.projectSettings

org.pragmaticdemo.sbt.HelloPlugin.helloKey := "new message"

org.pragmaticdemo.sbt.ScalastylePlugin.projectSettings

scalastyleConfig in Test :=  file("test.xml")

scalastyleConfig in Compile :=  file("scalastyle_config.xml")

incremental :=  true

