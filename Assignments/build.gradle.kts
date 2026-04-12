plugins {
    // Apply the Kotlin JVM plugin to the root project (but don't apply it directly)
    alias(libs.plugins.kotlin.jvm) apply false

    // Apply the ktlint plugin for code style checking (centralized version)
    id("org.jlleitschuh.gradle.ktlint") version "14.0.1" apply false
}
