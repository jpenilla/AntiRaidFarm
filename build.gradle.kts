plugins {
    `java-library`
    id("net.kyori.indra.git") version "3.0.1"
    id("kr.entree.spigradle") version "2.4.3"
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

group = "xyz.jpenilla"
version = "1.0.3+${lastCommitHash()}-SNAPSHOT"
description = "Break cheaty raid farms with a raid cooldown"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("com.destroystokyo.paper", "paper-api", "1.15.2-R0.1-SNAPSHOT")
    compileOnly("org.jetbrains", "annotations", "23.0.0")
}

tasks {
    compileJava {
        options.release.set(8)
    }
}

spigot {
    apiVersion = "1.15"
    website = "https://github.com/jmanpenilla/AntiRaidFarm"
    permissions.create("antiraidfarm.bypass") {
        description = "Bypasses raid farm cooldown"
        defaults = "false"
    }
    authors("jmp")
}

fun lastCommitHash(): String = indraGit.commit()?.name?.substring(0, 7)
  ?: error("Failed to determine git hash.")
