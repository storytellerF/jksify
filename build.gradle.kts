plugins {
    kotlin("jvm") version "2.0.21"
}

group = "com.storyteller_f"
version = "1.0-SNAPSHOT"

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}
