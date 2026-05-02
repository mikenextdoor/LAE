plugins {
    // Apply the org.jetbrains.kotlin.jvm Plugin to add support for Kotlin.
    alias(libs.plugins.kotlin.jvm)

    // Apply the application plugin to add support for building a CLI application in Java.
    application

    // Apply the ktlint plugin for code style checking.
    id("org.jlleitschuh.gradle.ktlint")
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {
    // Use the Kotlin Test integration.
    testImplementation(kotlin("test"))

    // H2 in-memory database for benchmarking
    implementation("com.h2database:h2:2.4.240")

    implementation(project(":utility"))

    implementation(kotlin("reflect"))
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(23)
    }
}

application {
    // Define the main class for the application.
    mainClass = "org.example.AppKt"
}
