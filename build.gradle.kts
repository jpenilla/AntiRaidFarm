import com.github.jengelman.gradle.plugins.shadow.tasks.ConfigureShadowRelocation
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.apache.commons.io.output.ByteArrayOutputStream

plugins {
    `java-library`
    id("com.github.johnrengelman.shadow") version "5.2.0"
    id("kr.entree.spigradle") version "2.1.1"
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

val projectName = "AntiRaidFarm"
group = "xyz.jpenilla"
version = "1.0.0+${getLastCommitHash()}-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
    maven(url = "https://papermc.io/repo/repository/maven-public/")
    maven(url = "https://oss.sonatype.org/content/groups/public/")
    maven(url = "https://repo.codemc.org/repository/maven-public")
    maven(url = "https://jitpack.io")
}

dependencies {
    compileOnly("com.destroystokyo.paper", "paper-api", "1.15.2-R0.1-SNAPSHOT")
    compileOnly("org.jetbrains", "annotations", "20.0.0")
    implementation("org.bstats", "bstats-bukkit", "1.7")
}

spigot {
    name = projectName
    apiVersion = "1.15"
    description = "Disable cheaty raid farms through a cooldown on bad omens"
    website = "https://github.com/jmanpenilla/AntiRaidFarm"
    permissions.create("antiraidfarm.bypass") {
        description = "Bypasses raid farm cooldown"
        defaults = "false"
    }
    authors("jmp")
}

val autoRelocate by tasks.register<ConfigureShadowRelocation>("configureShadowRelocation", ConfigureShadowRelocation::class) {
    target = tasks.getByName("shadowJar") as ShadowJar?
    val packageName = "${project.group}.${project.name.toLowerCase()}"
    prefix = "$packageName.shaded"
}

tasks {
    compileJava {
        options.compilerArgs.add("-parameters")
        options.isFork = true
        options.forkOptions.executable = "javac"
    }
    withType<ShadowJar> {
        archiveClassifier.set("")
        archiveFileName.set("$projectName-${project.version}.jar")
        dependsOn(autoRelocate)
        minimize()
    }
}

fun getLastCommitHash(): String {
    val byteOut = ByteArrayOutputStream()
    exec {
        commandLine = listOf("git", "rev-parse", "--short", "HEAD")
        standardOutput = byteOut
    }
    return byteOut.toString().trim()
}