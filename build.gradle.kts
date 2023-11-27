import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm") version libs.versions.kotlin.get()

    application
    alias(libs.plugins.johnrengelman.shadow)

    alias(libs.plugins.detekt)
    alias(libs.plugins.kotlinter)

    eclipse
    idea
}

buildscript {
    dependencies {
        classpath(libs.pinterest.ktlint)
    }
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

detekt {
    buildUponDefaultConfig = true
    config.from("${project.projectDir}/detekt.yaml")
    ignoreFailures = false
    parallel = true
}

tasks.detekt.configure {
    tasks.withType<Detekt>()
        .filter { it.project == project }
        .filterNot { it.name == name }
        .forEach { this.dependsOn(it) }
}

kotlinter {
    ignoreFailures = false
    reporters = arrayOf("plain", "checkstyle", "html")
}

application {
    mainClass.set("io.ysakhno.puzzles.cryptarithmetic.AppKt")
}
