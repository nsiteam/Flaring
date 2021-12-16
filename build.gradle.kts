import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.30"
    kotlin("plugin.serialization") version "1.5.30"
}

group = "com.nsiteam"
version = "1.0-SNAPSHOT"

repositories {
    maven("https://repo.osgeo.org/repository/geotools-releases/")
    mavenCentral()
}

dependencies {
    implementation(group = "org.jetbrains.kotlinx", name = "kotlinx-serialization-json", version = "1.2.2")
    implementation(group = "org.litote.kmongo", name = "kmongo-serialization", version = "4.3.0")
    implementation(group = "org.litote.kmongo", name = "kmongo", version = "4.3.0")
    implementation(group = "org.apache.commons", name = "commons-math3", version = "3.6.1")
    implementation(group = "org.apache.commons", name = "commons-csv", version = "1.9.0")
    implementation(group = "org.nield", name = "kotlin-statistics", version = "1.2.1")
    implementation(group = "org.geotools",name ="gt-shapefile",version = "26.0")
    implementation(group = "org.geotools",name ="gt-geojson",version = "26.0")
    implementation(group = "org.geotools",name ="gt-geojsondatastore",version = "26.0")
    implementation(group = "org.geotools",name ="gt-cql",version = "26.0")
    implementation(group = "org.locationtech.jts.io",name ="jts-io-common",version = "1.18.2")
    testImplementation(kotlin("test"))

}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
}