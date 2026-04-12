plugins {
    // Apply the org.jetbrains.kotlin.jvm Plugin to add support for Kotlin.
    alias(libs.plugins.kotlin.jvm)

    // Apply the ktlint plugin for code style checking.
    id("org.jlleitschuh.gradle.ktlint")

    // Apply the JMH plugin for benchmarking.
    id("me.champeau.jmh") version "0.7.3"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":app"))

    // H2 in-memory database for benchmarking
    implementation("com.h2database:h2:2.4.240")

    // Use the Kotlin Test integration.
    testImplementation(kotlin("test"))
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(23)
    }
}
