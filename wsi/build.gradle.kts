import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val ktorVersion = "1.3.1"
val koinVersion = "2.0.1"

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        val kotlinVersion = "1.3.21"
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    }
}

plugins {
    kotlin("jvm")
}

apply(plugin = "kotlin")

group = "wsi"

repositories {
    mavenCentral()
    jcenter()
    maven(url = "https://dl.bintray.com/kotlin/ktor")
}

dependencies {
    compile("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    compile("io.ktor:ktor-server-cio:$ktorVersion")
    compile("io.ktor:ktor-websockets:$ktorVersion")

    compile("org.koin:koin-core:$koinVersion")
    compile("org.koin:koin-core-ext:$koinVersion")

    compile("org.slf4j:slf4j-simple:1.6.1")
    compile("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.+")

    compile("com.natpryce:konfig:1.6.10.0")

    compile("io.ktor:ktor-html-builder:$ktorVersion")

    // https://mvnrepository.com/artifact/am.ik.yavi/yavi
    compile("am.ik.yavi:yavi:0.2.5")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
