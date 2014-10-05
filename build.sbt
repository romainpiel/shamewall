name := """play-java"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  javaWs
)

libraryDependencies += "com.puppycrawl.tools" % "checkstyle" % "5.7"

libraryDependencies += "org.eclipse.jgit" % "org.eclipse.jgit" % "3.5.0.201409260305-r"

libraryDependencies += "org.eclipse.jgit" % "org.eclipse.jgit.junit" % "3.5.0.201409260305-r" % "test"

libraryDependencies += "commons-io" % "commons-io" % "2.3"
