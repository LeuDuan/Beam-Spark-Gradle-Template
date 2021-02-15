import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption


plugins {
    id("project.java-commons")
    id("project.test-commons")
    id("project.deps-commons")
    kotlin("jvm") version "1.4.10"
    scala
}

group = "com.xpr"
version = "1.0"

repositories {
    mavenCentral()
}

val sparkVersion = "3.0.1"

dependencies {

    // see: https://cloud.google.com/dataproc/docs/concepts/versioning/dataproc-release-2.0
    implementation(project(":lib-transform"))
    implementation("org.scala-lang:scala-library:2.12.13")

    // spark deps
    val sparkVersion = "3.0.1"
    implementation("com.google.cloud.spark:spark-bigquery_2.12:0.18.1")
    implementation("org.yaml:snakeyaml:1.27")
    implementation("org.apache.spark:spark-core_2.12:$sparkVersion") {
        exclude("org.apache.hadoop", "hadoop-mapreduce-client-app")
        exclude("org.apache.curator")
    }
    implementation("org.apache.spark:spark-sql_2.12:$sparkVersion") {
        this.exclude("org.apache.hadoop")
    }
}

open class GreetingTask : DefaultTask() {
    @TaskAction
    fun greet() {
        println("hello from GreetingTask")
    }
}

// Create a task using the task type
tasks.register<GreetingTask>("hello")

tasks.create("execute-on-dataproc", Exec::class) {
    val workingDir = Paths.get("$projectDir")
    setWorkingDir(workingDir.toFile())
    val jar = Paths.get("$projectDir/build/libs/spark-code-1.0.jar")
    val target = Paths.get("$projectDir/lib/spark-code-1.0.jar")
    Files.copy(jar, target, StandardCopyOption.REPLACE_EXISTING)
    val jarFiles = run {
        val dir = Paths.get("$projectDir/lib")
        Files.newDirectoryStream(dir, "*.jar").map { "lib/${it.fileName}" }
            .plus("gs://spark-lib/bigquery/spark-bigquery-latest_2.12.jar")
    }
        .joinToString(separator=",", prefix="", postfix="")
    println(jarFiles)
    commandLine(
        "cmd",
        "/c",
        "gcloud",
        "dataproc",
        "jobs",
        "submit",
        "spark",
        "--project",
        "[project]",
        "--cluster=[cluster-name]",
        "--region=us-central1",
        "--class=com.spaki.Main",
        "--jars=$jarFiles",
        "--",
        "gs://apache-beam-samples/shakespeare/kinglear.txt"
    )
}.dependsOn("jar", "pull-jars")

tasks.create("pull-jars", DefaultTask::class) {
    this.description = "pull jars from lib-transform in order to make sbt work"
    doLast {
        println("copying jars")
        val libTransformJar =
            project(":lib-transform").buildDir.toPath().resolve("libs/lib-transform-1.0.jar")
        val target = Paths.get("$projectDir/lib/lib-transform-1.0.jar")
        Files.copy(libTransformJar, target, StandardCopyOption.REPLACE_EXISTING)
    }
}.dependsOn(":lib-transform:jar")
