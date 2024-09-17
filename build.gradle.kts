plugins {
    kotlin("jvm") version "2.0.10"
}

group = "dev.okano.kotlintesting"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

object DependencyVersions {
    const val COROUTINES = "1.9.0"
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${DependencyVersions.COROUTINES}")

    testImplementation(kotlin("test"))
    testImplementation("io.kotest:kotest-assertions-core-jvm:5.9.1")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}