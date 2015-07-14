
lazy val root =
	(project in file("."))
	.settings(
		name := "asyncpools",

		version := "0.0.4",

		scalaVersion := "2.11.7",

		resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",

		libraryDependencies ++= Seq(
			"com.typesafe.akka" % "akka-actor_2.11" % "2.3.11",

			"org.specs2" %% "specs2-core" % "3.6.2" % "test"
		),

		resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases",

		scalacOptions ++= Seq(
			"-unchecked",
			"-deprecation",
			"-feature",
			"-Xfatal-warnings",
			"-Ywarn-dead-code",
			"-Xmax-classfile-name", "140"
		),

		scalacOptions in Test ++= Seq("-Yrangepos")

)
