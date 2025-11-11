import com.vanniktech.maven.publish.JavaLibrary
import com.vanniktech.maven.publish.JavadocJar

plugins {
    `java-library`
    jacoco
    id("io.github.stellarsunset.auto-semver") version "2.0.0"
    id("com.vanniktech.maven.publish") version "0.35.0"
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

    testImplementation(libs.junit.jupiter)
    testImplementation(libs.grib)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(23)
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)

    reports {
        xml.required = true
        html.required = true
    }
}

tasks.check {
    dependsOn(tasks.jacocoTestReport)
}

mavenPublishing {
    configure(JavaLibrary(javadocJar = JavadocJar.Javadoc(), sourcesJar = true))

    publishToMavenCentral(automaticRelease = true)

    coordinates("io.github.stellarsunset", "netcdf", project.version.toString())

    pom {
        name = "netcdf"
        description = "A thin wrapper around NetCDF-Java for reading sub-schemas of variables into Java objects."
        url = "https://github.com/stellarsunset/netcdf"
        licenses {
            license {
                name = "The Apache License, Version 2.0"
                url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
            }
        }
        developers {
            developer {
                id = "stellarsunset"
                name = "Alex Cramer"
                email = "stellarsunset@proton.me"
            }
        }
        scm {
            connection = "scm:git:git://github.com/stellarsunset/netcdf.git"
            developerConnection = "scm:git:ssh://github.com/stellarsunset/netcdf.git"
            url = "http://github.com/stellarsunset/netcdf"
        }
    }

    signAllPublications()
}
