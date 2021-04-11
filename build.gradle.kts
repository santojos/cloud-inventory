import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {

    kotlin("jvm") version "1.3.50"
    kotlin("kapt") version "1.3.50"
    application
}

group = "me.santoshjoshi"
version = "1.0-SNAPSHOT"

configure<ApplicationPluginConvention> {
    mainClassName = "KotlinApplication"
}

repositories {
    mavenCentral()
}

dependencies {
    kapt("com.google.dagger:dagger-compiler:2.26")
    api("com.google.dagger:dagger:2.26")
    annotationProcessor("com.google.dagger:dagger-compiler:2.26")

    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.0")

    // https://mvnrepository.com/artifact/com.google.code.gson/gson
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("com.oracle.oci.sdk:oci-java-sdk-full:1.34.0")
    //implementation("com.google.dagger:dagger:2.26")
    implementation("io.github.microutils:kotlin-logging-jvm:2.0.6")
    //implementation("org.slf4j:slf4j-api:2.0.0-alpha1")
    implementation("ch.qos.logback:logback-classic:1.2.3")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}