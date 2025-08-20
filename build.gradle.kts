plugins {
    kotlin("jvm") version "2.2.0"
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
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}
