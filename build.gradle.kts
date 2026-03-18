plugins {
    id("java-library")
    id("org.allaymc.gradle.plugin") version "0.2.1"
}

group = "id.brokiem.pvpbot"
description = "Java plugin template for allay server"
version = "0.1.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

// See also https://github.com/AllayMC/AllayGradle
allay {
    // You can find the latest version here: https://central.sonatype.com/artifact/org.allaymc.allay/api
    api = "0.26.0"

    plugin {
        entrance = ".PvPBot"
        authors += "brokiem"
        website = "https://github.com/brokiem/pvp-bot"
    }
}

dependencies {
    compileOnly(group = "org.projectlombok", name = "lombok", version = "1.18.34")
    annotationProcessor(group = "org.projectlombok", name = "lombok", version = "1.18.34")
}
