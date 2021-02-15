plugins {
    id("project.java-commons")
    id("project.test-commons")
    id("project.deps-commons")
    application
    kotlin("jvm") version "1.4.10"
}

group = "com.xpr"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {

    implementation(project(":lib-transform"))
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))

    // beam deps
    val apacheBeamVersion = "2.27.0"
    implementation("org.apache.beam:beam-sdks-java-core:$apacheBeamVersion")
    implementation("org.apache.beam:beam-sdks-java-io-google-cloud-platform:$apacheBeamVersion") {
        exclude("com.google.cloud", "google-cloud-spanner")
        exclude("com.google.cloud", "google-cloud-pubsub")
        exclude("com.google.cloud", "google-cloud-pubsublite")
        exclude("com.google.cloud.bigtable")
        exclude("com.google.cloud.datastore")
    }
    runtimeOnly("org.apache.beam:beam-runners-direct-java:$apacheBeamVersion")
    implementation("org.apache.beam:beam-sdks-java-extensions-kryo:$apacheBeamVersion")

    implementation("org.slf4j:slf4j-api:1.7.25")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.13.2")
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("org.yaml:snakeyaml:1.27")
}

tasks.create("simple-task") {
    this.doFirst {
        println("simple-task do first")
    }
}

application {
    this.mainClass.set("com.syntax.OneKt")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
}

val CLASS_PATH = project.tasks[JavaPlugin.JAR_TASK_NAME].outputs.files + project.configurations.runtimeClasspath

tasks.create("one", JavaExec::class) {
    this.mainClass.set("com.syntax.OneKt")
    this.args = listOf("one", "two")
    this.environment["GOOGLE_APPLICATION_CREDENTIALS"] = "[sa]"
    if (System.getProperty("exec.args") != null) {
        this.args = System.getProperty("exec.args").split(" ")
    }
    this.classpath = CLASS_PATH
}.dependsOn("compileKotlin")

tasks.create("k-word-count", JavaExec::class) {
    this.mainClass.set("com.beam_kotlin.word_count.MainKt")
    this.environment["KEY"] = "VALUE"
    this.args = listOf("--input=gs://apache-beam-samples/shakespeare/kinglear.txt")
    if (System.getProperty("exec.args") != null) {
        this.args = System.getProperty("exec.args").split(" ")
    }
    this.classpath = CLASS_PATH
}.dependsOn("compileKotlin")
