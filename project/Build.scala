import sbt._
import sbt.Keys._
import play.sbt.PlayImport._
import play._
import play.sbt.PlayScala

object ApplicationBuild extends Build {

  lazy val branch = "git rev-parse --abbrev-ref HEAD".!!.trim
  lazy val commit = "git rev-parse --short HEAD".!!.trim
  lazy val buildTime = new java.text.SimpleDateFormat("yyyyMMdd-HHmmss").format(new java.util.Date())
  lazy val appVersion = "%s-%s-%s".format(branch, commit, buildTime)




  lazy val commonDependencies = Seq(
    // Add your project dependencies here,
    jdbc,
    specs2 % Test,
    "org.json4s" %% "json4s-jackson" % "3.6.3"
   )
  lazy val serviceADependencies = Seq() // You can have service specific dependencies
  lazy val serviceBDependencies = Seq()

  lazy val scalaBuildOptions = Seq("-unchecked", "-deprecation", "-feature", "-language:reflectiveCalls",
    "-language:implicitConversions", "-language:postfixOps", "-language:dynamics","-language:higherKinds",
    "-language:existentials", "-language:experimental.macros", "-Xmax-classfile-name", "140")


  lazy val common = Project("common", file("modules/common")).enablePlugins(PlayScala).settings(
    // Add common settings here
    version := appVersion,
    libraryDependencies += specs2 % Test,
    scalacOptions ++= scalaBuildOptions,
    sources in doc in Compile := List(),
    javaOptions in Test += "-Dconfig.resource=common-application.conf"
  )

  lazy val apiLayer = Project("apilayer", file("modules/apilayer")).enablePlugins(PlayScala).settings(
    // Add serviceA settings here
    version := appVersion,
    libraryDependencies ++= commonDependencies,
    scalacOptions ++= scalaBuildOptions,
    sources in doc in Compile := List(),
    javaOptions in Test += "-Dconfig.resource=application.conf"
  ).dependsOn(common % "test->test;compile->compile").aggregate(common)

  lazy val dataLayer = Project("datalayer", file("modules/datalayer")).enablePlugins(PlayScala).settings(
    // Add serviceB settings here
    version := appVersion,
    libraryDependencies ++= commonDependencies,
    scalacOptions ++= scalaBuildOptions,
    sources in doc in Compile := List(),
    javaOptions in Test += "-Dconfig.resource=application.conf"
  ).dependsOn(common % "test->test;compile->compile").aggregate(common)


  // The default SBT project is based on the first project alphabetically. To force sbt to use this one,
  // we prefix it with 'aaa'
  lazy val aaaMultiProject = Project("WeatherMicroApp", file(".")).settings(
    version := appVersion,
    libraryDependencies ++= commonDependencies,
    // This project runs both services together, which is mostly useful in development mode.
    scalacOptions ++= scalaBuildOptions,
    sources in doc in Compile := List()
  ).dependsOn(common % "test->test;compile->compile", apiLayer % "test->test;compile->compile", dataLayer % "test->test;compile->compile").aggregate(common, apiLayer, dataLayer)

}
