import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.dokka.DokkaConfiguration.Visibility.INTERNAL
import org.jetbrains.dokka.DokkaConfiguration.Visibility.PROTECTED
import org.jetbrains.dokka.DokkaConfiguration.Visibility.PUBLIC
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm") version libs.versions.kotlin.get()
    alias(libs.plugins.kotlinx.kover)
    alias(libs.plugins.dokka)

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

tasks.withType<DokkaTask>().configureEach {
    failOnWarning.set(true)
    dokkaSourceSets.configureEach {
        reportUndocumented.set(true)
        documentedVisibilities.set(setOf(PUBLIC, PROTECTED, INTERNAL))
        jdkVersion.set(libs.versions.javaLanguageCompatibility.get().toInt())
    }
}

tasks.test.configure {
    useJUnitPlatform()
}

koverReport {
    defaults {
        html {
            onCheck = true
        }
        xml {
            onCheck = true
        }
    }
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

tasks.build.configure {
    dependsOn(tasks.dokkaHtml)
}

application {
    mainClass.set("io.ysakhno.puzzles.cryptarithmetic.AppKt")
}
