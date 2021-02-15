plugins {
    java
}

dependencies {
    implementation(platform("com.fasterxml.jackson:jackson-bom:2.12.1"))
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.4.10"))
    constraints {
        implementation("org.apache.avro:avro:1.9.2")
        implementation("com.google.cloud:google-cloud-bigquery:1.123.2")
        implementation("com.google.cloud:google-cloud-bigquerystorage:1.6.0") // 1.6.7 doesn't work
        implementation("commons-codec:commons-codec:1.15")
    }
}