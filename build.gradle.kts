import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.*

object Defs {
    const val kotlinVersion = "1.3.21"
    const val ktorVersion = "1.3.1"
    const val logbackVersion = "1.2.3"
    const val koinVersion = "2.0.1"
    const val junitVersion = "5.4.2"
    const val exposedVersion = "0.20.1"
    const val slf4jVersion = "1.6.1"
    const val jacksonVersion = "2.9.+"
    const val konfigVersion = "1.6.10.0"
    const val argonVersion = "2.1"
    const val yaviVersion = "0.2.5"
    const val pgVersion = "42.2.2"
    const val liquibaseVersion = "3.4.1"
    const val snakeyamlVersion = "1.25"
}

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.21")
    }
}

subprojects {
    version = "1.0"
}

plugins {
    kotlin("jvm") version "1.3.21"
    id("org.liquibase.gradle") version "2.0.1"
}

apply(plugin = "kotlin")
apply(plugin = "liquibase")

repositories {
    mavenCentral()
    jcenter()
    maven(url = "https://dl.bintray.com/kotlin/ktor")
}

fun exposedDep(packageName: String) = "org.jetbrains.exposed:$packageName:${Defs.exposedVersion}"

fun getPropertyOrEnv(key: String): Any? {
    val props = Properties()
    val propertiesFile = File("$projectDir/src/main/resources/application.properties")
    if (propertiesFile.exists()) {
        props.load(propertiesFile.inputStream())

        return props[key]
    }

    return System.getenv()[key]
}

dependencies {
    compile("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    compile("org.jetbrains.kotlin:kotlin-reflect")

    compile("io.ktor:ktor-server-cio:${Defs.ktorVersion}")
    compile("io.ktor:ktor-websockets:${Defs.ktorVersion}")
    compile("io.ktor:ktor-html-builder:${Defs.ktorVersion}")

    compile("org.koin:koin-core:${Defs.koinVersion}")
    compile("org.koin:koin-core-ext:${Defs.koinVersion}")

    compile("org.slf4j:slf4j-simple:${Defs.slf4jVersion}")
    compile("com.fasterxml.jackson.module:jackson-module-kotlin:${Defs.jacksonVersion}")

    compile("com.natpryce:konfig:${Defs.konfigVersion}")

    testImplementation("org.junit.jupiter:junit-jupiter-api:${Defs.junitVersion}")
    implementation("org.junit.jupiter:junit-jupiter-engine:${Defs.junitVersion}")

    // https://mvnrepository.com/artifact/de.mkammerer/argon2-jvm
    compile("de.mkammerer:argon2-jvm:${Defs.argonVersion}")

    // https://mvnrepository.com/artifact/am.ik.yavi/yavi
    compile("am.ik.yavi:yavi:${Defs.yaviVersion}")

    compile(exposedDep("exposed-core"))
    compile(exposedDep("exposed-dao"))
    compile(exposedDep("exposed-jdbc"))
    compile(exposedDep("exposed-jodatime"))
    compile("org.postgresql:postgresql:${Defs.pgVersion}")
    compile(project(":wsi"))

    liquibaseRuntime("org.liquibase:liquibase-core:${Defs.liquibaseVersion}")
    liquibaseRuntime("org.yaml:snakeyaml:${Defs.snakeyamlVersion}")
    liquibaseRuntime("org.postgresql:postgresql:${Defs.pgVersion}")
}


tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<Test> {
    useJUnitPlatform()
}

liquibase {
    activities.create("main") {
        val changeLogFile = getPropertyOrEnv("LIQUIBASE_CHANGE_LOG_FILE")
        val dbUsername = getPropertyOrEnv("DB_USERNAME")
        val dbPassword = getPropertyOrEnv("DB_PASSWORD")
        val dbHost = getPropertyOrEnv("DB_HOST")
        val dbDriver = getPropertyOrEnv("DB_DRIVER")
        this.arguments = mapOf(
            "driver" to dbDriver,
            "logLevel" to "info",
            "changeLogFile" to changeLogFile,
            "url" to dbHost,
            "username" to dbUsername,
            "password" to dbPassword
        )
    }
}