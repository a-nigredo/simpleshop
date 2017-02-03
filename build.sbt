name := "simpleshop"

version := "1.0"

scalaVersion := "2.12.1"

mainClass in assembly := Some("dev.nigredo.Application")

assemblyJarName in assembly := "shop.jar"

scalacOptions ++= Seq("-feature", "-deprecation", "-language:implicitConversions", "-language:higherKinds", "-unchecked", "-language:postfixOps", "-language:existentials")

val akkaVersion = "2.4.16"
val akkaHttpVersion = "10.0.1"
val akkaLogVersion = "2.4.16"
val json4sVersion = "3.5.0"
val scalaLoggingVersion = "3.5.0"
val reactiveMongoVersion = "0.12.1"
val scalaTestVersion = "3.0.1"
val akkaHttpJson = "1.12.0"
val logbackVersion = "1.0.0"
val bcryptVersion = "3.0"
val scalazVersion = "7.2.8"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % "test",
  "com.typesafe.akka" %% "akka-http-core" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % "test",
  "com.typesafe.akka" %% "akka-slf4j" % akkaLogVersion,
  "org.json4s" %% "json4s-native" % json4sVersion,
  "org.json4s" %% "json4s-ext" % json4sVersion,
  "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion,
  "org.reactivemongo" %% "reactivemongo" % reactiveMongoVersion,
  "org.scalactic" %% "scalactic" % scalaTestVersion,
  "org.scalatest" %% "scalatest" % scalaTestVersion % "test",
  "de.heikoseeberger" %% "akka-http-json4s" % akkaHttpJson,
  "ch.qos.logback" % "logback-classic" % logbackVersion % "runtime",
  "com.github.t3hnar" %% "scala-bcrypt" % bcryptVersion,
  "org.scalaz" %% "scalaz-core" % scalazVersion
)
