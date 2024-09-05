import xyz.jpenilla.resourcefactory.bukkit.Permission

plugins {
    `java-library`
    id("net.kyori.indra.git") version "3.1.3"
    id("xyz.jpenilla.resource-factory-bukkit-convention") version "1.2.0"
    id("xyz.jpenilla.run-paper") version "2.3.0"
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

group = "xyz.jpenilla"
version = "1.0.4-SNAPSHOT+${lastCommitHash()}"
description = "Break cheaty raid farms with a raid cooldown"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("com.destroystokyo.paper", "paper-api", "1.15.2-R0.1-SNAPSHOT")
}

tasks {
    runServer {
        minecraftVersion("1.19.3")
    }
    compileJava {
        options.release.set(8)
        options.encoding = Charsets.UTF_8.name()
    }
}

bukkitPluginYaml {
    main = "xyz.jpenilla.antiraidfarm.AntiRaidFarm"
    apiVersion = "1.15"
    website = "https://github.com/jpenilla/AntiRaidFarm"
    permissions.register("antiraidfarm.bypass") {
        description = "Bypasses raid farm cooldown"
        default = Permission.Default.FALSE
    }
    authors = listOf("jmp")
}

fun lastCommitHash(): String = indraGit.commit()?.name?.substring(0, 7)
  ?: error("Failed to determine git hash.")
