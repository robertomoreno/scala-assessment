name := "Scala Assessment"

organization := "assessment"

version := "1.0.0"

scalaVersion := "2.11.7"

// Twitter Server
resolvers += "twttr" at "https://maven.twttr.com/"

libraryDependencies ++= {
  val akkaVersion     = "2.4.10"
  val scalaTestVersion = "3.0.0"
  val scalatraVersion = "2.4.+"

  Seq(
    "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
    "org.scalatra" %% "scalatra" % scalatraVersion,
    "org.scalatra" %% "scalatra-scalate" % scalatraVersion,
    "org.eclipse.jetty" % "jetty-webapp" % "9.2.14.v20151106" % "compile",

    "org.eclipse.jetty.orbit" % "javax.servlet" % "3.0.0.v201112011016" % "compile;provided;test" artifacts (Artifact("javax.servlet", "jar", "jar")),
    "org.slf4j" % "slf4j-simple" % "1.7.21",

    "org.scalatra" %% "scalatra-scalatest" % scalatraVersion % "test",
    "org.scalatest" %% "scalatest" % scalaTestVersion % "test"
  )
}
