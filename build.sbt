name := """kryo-test"""

version := "1.0"

scalaVersion := "2.10.4"

// Change this to another test framework if you prefer
libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.4" % "test"
libraryDependencies += "com.esotericsoftware" % "kryo" % "3.0.0"
// Uncomment to use Akka
//libraryDependencies += "com.typesafe.akka" % "akka-actor_2.11" % "2.3.9"

