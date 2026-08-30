plugins {
    `java-library`
    id("io.github.stellarsunset.java-conventions") version "0.0.9"
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://artifacts.unidata.ucar.edu/repository/unidata-all/")
        mavenContent {
            releasesOnly()
        }
    }
}

dependencies {

    api(libs.cdm.core)
    api(libs.commons)
    implementation(libs.guava)
    implementation(libs.slf4j.api)

    // For @SuppressFBWarnings; compile-only since SpotBugs only reads it from bytecode.
    compileOnly("com.github.spotbugs:spotbugs-annotations:4.10.4")

    testImplementation(libs.junit.jupiter)
    testImplementation(libs.grib)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
