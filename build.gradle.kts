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

val projectName = "AntiRaidFarm"
group = "xyz.jpenilla"
version = "1.0.2+${getLastCommitHash()}-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
    maven(url = "https://papermc.io/repo/repository/maven-public/")
    maven(url = "https://oss.sonatype.org/content/groups/public/")
    maven(url = "https://jitpack.io")
}

dependencies {
    compileOnly("com.destroystokyo.paper", "paper-api", "1.15.2-R0.1-SNAPSHOT")
    compileOnly("org.jetbrains", "annotations", "20.0.0")
}

spigot {
    name = projectName
    apiVersion = "1.15"
    description = "Break cheaty raid farms with a raid cooldown"
    website = "https://github.com/jmanpenilla/AntiRaidFarm"
    permissions.create("antiraidfarm.bypass") {
        description = "Bypasses raid farm cooldown"
        defaults = "false"
    }
    authors("jmp")
}

val autoRelocate by tasks.register<ConfigureShadowRelocation>("configureShadowRelocation", ConfigureShadowRelocation::class) {
    target = tasks.shadowJar.get()
    val packageName = "${project.group}.${project.name.toLowerCase()}"
    prefix = "$packageName.shaded"
}

tasks {
    build {
        dependsOn(shadowJar)
    }
    shadowJar {
        minimize()
        dependsOn(autoRelocate)
        archiveClassifier.set("")
        archiveFileName.set("$projectName-${project.version}.jar")
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