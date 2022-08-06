import ru.endlesscode.bukkitgradle.dependencies.spigot
import ru.endlesscode.bukkitgradle.dependencies.spigotApi

plugins {
    id("ru.endlesscode.bukkitgradle") version "0.10.1"
}

group = "ru.endlesscode.markitem"
description = "Mark your items"
version = "0.5"

repositories {
    mavenCentral()
    spigot()
}

bukkit {
    apiVersion = "1.19.1"

    server {
        setCore("paper")
        eula = true
    }

    meta {
        name.set("MarkItem")
        authors.addAll("osipxd", "Dereku")
        apiVersion.set("1.14")
    }
}

dependencies {
    compileOnly(spigotApi)
    compileOnly("org.jetbrains:annotations:23.0.0")
}
