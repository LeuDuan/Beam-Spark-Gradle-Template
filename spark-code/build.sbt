name := "dev-only"

version := "1.0"

scalaVersion := "2.12.13"

libraryDependencies += "org.apache.spark" %% "spark-sql" % "3.0.1"
libraryDependencies += "org.apache.spark" %% "spark-core" % "3.0.1"
libraryDependencies += "org.yaml" % "snakeyaml" % "1.27"
libraryDependencies += "com.google.cloud.spark" %% "spark-bigquery" % "0.18.1"
// https://mvnrepository.com/artifact/org.jetbrains.kotlin/kotlin-stdlib
libraryDependencies += "org.jetbrains.kotlin" % "kotlin-stdlib" % "1.4.10"

mainClass in (Compile) := Some("com.spaki.Main")
