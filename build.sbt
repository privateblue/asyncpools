name := "asyncpools"

version := "0.0.3"

scalaVersion := "2.10.2"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
    "com.typesafe.akka" % "akka-actor_2.10" % "2.2.3"
)
