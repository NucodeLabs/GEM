plugins {
    kotlin("jvm")
}

group = "ru.nucodelabs"
version = rootProject.version

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}