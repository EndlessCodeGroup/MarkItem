plugins {
    java
}

group = "ru.endlesscode.markitem"
version = "0.5"
description = "Mark your items"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.19.1-R0.1-SNAPSHOT")
    compileOnly("org.jetbrains:annotations:23.0.0")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}
