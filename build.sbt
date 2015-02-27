name := """serialization-test"""

version := "1.0"

scalaVersion := "2.11.5"

resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"
resolvers += "jitpack" at "https://jitpack.io"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.4-SNAPSHOT"
libraryDependencies += "com.esotericsoftware" % "kryo" % "3.0.0"
libraryDependencies += "com.twitter" % "chill_2.11" % "0.5.2"
libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.10"

libraryDependencies ++= Seq(
  "org.apache.thrift" % "libthrift" % "0.8.0",
  "com.twitter" % "scrooge-core_2.11" % "3.17.0",
  "com.twitter" % "bijection-core_2.11" % "0.7.2",
  "com.twitter" % "bijection-scrooge_2.11" % "0.7.2"
)

com.twitter.scrooge.ScroogeSBT.newSettings
unmanagedSourceDirectories in Compile += baseDirectory.value / "target" / "scala-2.11" / "src_managed"
