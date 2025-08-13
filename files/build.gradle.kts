plugins {
    kotlin("jvm")
}

group = "ru.nucodelabs"
version = rootProject.version

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":common-utils"))

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
    workingDir(file(rootProject.ext["runDir"]!!))
    dependsOn(":copyDataToRunDir")
    finalizedBy(":cleanRunDir")
}