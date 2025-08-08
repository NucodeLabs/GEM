plugins {
    kotlin("jvm")
}

group = "ru.nucodelabs"
version = "2.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":common-utils"))

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}