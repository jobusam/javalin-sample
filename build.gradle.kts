import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.50"
    application
}

group = "de.busam"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    compileOnly(kotlin("reflect"))
    implementation("io.javalin","javalin","3.6.0")
    implementation("org.slf4j","slf4j-simple","1.7.28")
    implementation("com.fasterxml.jackson.module","jackson-module-kotlin","2.8.4")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application{
    mainClassName = "de.busam.app.AppKt"
}

// for a proper deployment of the configuration files an key stores.
// Keep in mind the folder structure is directly referenced in code to work
// from binary distribution and within IDE.
// Caution: To support the distribution plugin from a newly created gradle project
// you have to update the gradle version! Because per default IntelliJ uses
// the gradle wrapper version 4.10. But at least gradle 5.0 is required.
// 1) Close Idea and clean project with $ ./gradlew clean
// 2) Delete .iml and .gradle folder
// 3) Update gradle with $ ./gradlew wrapper --gradle-version 5.4.1 --distribution-type all
// 4) Reimport gradle project into IntelliJ Idea
distributions {
   main {
        contents {
            // relative to project root -> copy every file in resources folder
            from("resources") {
                // relative to root folder in generated distribution structure
                into("bin/resources")
            }
        }
    }
}
