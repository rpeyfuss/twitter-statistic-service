name := """twitterstatisticservice"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.0"

libraryDependencies ++= Seq(guice, ws, filters,
    "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.3" % Test,
    "org.lyranthe.prometheus" %% "client" % "0.9.0-M5",
    "com.github.stijndehaes" %% "play-prometheus-filters" % "0.6.0-rc1",
    "io.zipkin.brave.play" %% "play-zipkin-tracing-play" % "3.0.1",
    "org.webjars" % "swagger-ui" % "3.22.2",
    "io.honeycomb.libhoney" % "libhoney-java" % "1.1.0"
)
