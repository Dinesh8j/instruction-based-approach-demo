name := "forecast-framework"
version := "1.0.0"
scalaVersion := "2.13.12"

libraryDependencies ++= Seq(
  "org.yaml"                   %  "snakeyaml"       % "2.2",
  "com.typesafe.play"          %% "play-json"        % "2.10.6",
  "org.slf4j"                  %  "slf4j-api"        % "2.0.9",
  "ch.qos.logback"             %  "logback-classic"  % "1.4.14"
)