name := "asyncpools"

version := "0.0.4"

scalaVersion := "2.11.7"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
    "com.typesafe.akka" % "akka-actor_2.11" % "2.3.11"
)
