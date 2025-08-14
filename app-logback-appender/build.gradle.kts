plugins {
    kotlin("jvm")
}

group = "ru.nucodelabs"
version = rootProject.version

repositories {
    mavenCentral()
}

dependencies {
    implementation("ch.qos.logback:logback-classic:${rootProject.properties["logbackVersion"]}")
    implementation(project(":common-utils"))

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}