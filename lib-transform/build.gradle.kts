plugins {
    id("project.java-commons")
    id("project.test-commons")
    id("project.deps-commons")
    kotlin("jvm") version "1.4.10"
}

group = "com.xpr"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    implementation(kotlin("script-runtime"))
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("commons-codec:commons-codec:1.15")
}
