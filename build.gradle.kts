import ru.endlesscode.bukkitgradle.dependencies.spigot
import ru.endlesscode.bukkitgradle.dependencies.spigotApi

plugins {
    id("ru.endlesscode.bukkitgradle") version "0.10.1"
}

group = "ru.endlesscode.markitem"
description = "Mark your items"
version = "1.0-rc1"

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
        main.set("$group.MarkItemPlugin")
        name.set("MarkItem")
        authors.addAll("osipxd", "Dereku")
        apiVersion.set("1.14")
    }
}

dependencies {
    compileOnly(spigotApi)
    compileOnly("org.jetbrains:annotations:23.0.0")
    compileOnly("ru.endlesscode.mimic:mimic-bukkit-api:0.8.0")
}
