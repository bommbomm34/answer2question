import org.gradle.internal.declarativedsl.parsing.main

plugins {
    kotlin("jvm") version "2.2.0"
    application
}

group = "io.github.bommbomm34.answer2question"
version = "0.0.1"
val coreNLPVersion = "4.5.10"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("edu.stanford.nlp:stanford-corenlp:$coreNLPVersion")
    implementation("edu.stanford.nlp:stanford-corenlp:$coreNLPVersion:models")
    implementation("com.github.ajalt.clikt:clikt:5.0.3")
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("MainKt")
}

kotlin {
    jvmToolchain(21)
}
