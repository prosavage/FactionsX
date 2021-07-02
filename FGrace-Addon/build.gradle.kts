import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    id("com.github.johnrengelman.shadow")
}

description = "Implement a virtual shared faction chest."


dependencies {
    compileOnly(project(":FactionsX"))
    compileOnly(project(":AddonFramework"))
    compileOnly("me.clip:placeholderapi:2.10.9")
    compileOnly("net.prosavage:BasePlugin:1.7.6")
    compileOnly("com.github.cryptomorin:XSeries:8.1.0")
    compileOnly("org.spigotmc:spigot-api:1.17-R0.1-SNAPSHOT")
}

tasks {

    compileJava {
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
    }

    val shadowJar = named<ShadowJar>("shadowJar") {
        mergeServiceFiles()
        minimize()

        val shadePath = "net.prosavage.factionsx.shade"
        relocate("kotlin", "$shadePath.kotlin")
        relocate("com.cryptomorin.xseries", "$shadePath.xseries")
        archiveFileName.set("FGrace-Addon-${project.version}.jar")
    }
}