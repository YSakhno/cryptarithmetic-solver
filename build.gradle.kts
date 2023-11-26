import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm") version libs.versions.kotlin.get()

    application
    alias(libs.plugins.johnrengelman.shadow)

    eclipse
    idea
}

eclipse {
    classpath {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.coroutines.core)

    testImplementation(libs.bundles.kotest)
    testRuntimeOnly(libs.kotest.runner)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.javaLanguageCompatibility.get()))
    }
}

kotlin {
    compilerOptions {
        allWarningsAsErrors.set(true)
        jvmTarget.set(JvmTarget.fromTarget(libs.versions.javaLanguageCompatibility.get()))
    }
}

tasks.test.configure {
    useJUnitPlatform()
}

application {
    mainClass.set("io.ysakhno.puzzles.cryptarithmetic.AppKt")
}
