import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
}

description = "Adds more crop upgrades into factionsx."

dependencies {
    compileOnly(project(":FactionsX"))
    compileOnly(project(":AddonFramework"))
    compileOnly("net.prosavage:BasePlugin:1.7.6")
    compileOnly("io.papermc:paperlib:1.0.2")
    compileOnly("org.spigotmc:spigot-api:1.17-R0.1-SNAPSHOT")
    compileOnly("com.github.cryptomorin:XSeries:8.1.0")
    compileOnly(kotlin("stdlib-jdk8"))
}


tasks {
    val shadowJar = named<ShadowJar>("shadowJar") {
        mergeServiceFiles()
        minimize()

        val shadePath = "net.prosavage.factionsx.shade"
        relocate("kotlin", "$shadePath.kotlin")
        relocate("io.papermc.lib", "$shadePath.paperlib")
        relocate("com.cryptomorin.xseries", "$shadePath.xseries")
        archiveFileName.set("FCropUpgrades-Addon-${project.version}.jar")
    }
}