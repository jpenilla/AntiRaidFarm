import com.github.jengelman.gradle.plugins.shadow.tasks.ConfigureShadowRelocation
import org.apache.commons.io.output.ByteArrayOutputStream

plugins {
    `java-library`
    id("com.github.johnrengelman.shadow") version "6.1.0"
    id("kr.entree.spigradle") version "2.2.3"
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

group = "xyz.jpenilla"
version = "1.0.2+${getLastCommitHash()}-SNAPSHOT"
description = "Break cheaty raid farms with a raid cooldown"

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("com.destroystokyo.paper", "paper-api", "1.15.2-R0.1-SNAPSHOT")
    compileOnly("org.jetbrains", "annotations", "20.1.0")
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

tasks {
    build {
        dependsOn(shadowJar)
    }
    val autoRelocate by register("configureShadowRelocation", ConfigureShadowRelocation::class) {
        target = shadowJar.get()
        val packageName = "${project.group}.${project.name.toLowerCase()}"
        prefix = "$packageName.lib"
    }
    shadowJar {
        minimize()
        dependsOn(autoRelocate)
        archiveClassifier.set("")
        archiveFileName.set("${rootProject.name}-${project.version}.jar")
    }
}

fun getLastCommitHash(): String {
    val byteOut = ByteArrayOutputStream()
    exec {
        commandLine = listOf("git", "rev-parse", "--short", "HEAD")
        standardOutput = byteOut
    }
    return byteOut.toString(Charsets.UTF_8).trim()
}