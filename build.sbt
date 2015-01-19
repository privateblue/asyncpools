name := "asyncpools"

version := "0.0.3"

scalaVersion := "2.10.2"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
    "com.typesafe.akka" % "akka-actor_2.10" % "2.2.3",
    "com.typesafe.slick" %% "slick" % "1.0.1",
    "com.typesafe" % "config" % "1.0.0",
    "com.h2database" % "h2" % "1.3.167",
    "org.specs2" %% "specs2" % "2.2.1" % "test"
)
