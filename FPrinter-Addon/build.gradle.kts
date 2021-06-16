import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
}


dependencies {
    compileOnly(project(":FactionsX"))
    compileOnly(project(":AddonFramework"))
    compileOnly("me.clip:placeholderapi:2.10.4")
    compileOnly("net.prosavage:BasePlugin:1.7.6")
    compileOnly("io.papermc:paperlib:1.0.2")
    compileOnly("com.github.cryptomorin:XSeries:7.2.1")
    compileOnly("org.spigotmc:spigot-api:1.16.4-R0.1-SNAPSHOT")
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
        archiveFileName.set("FPrinter-Addon-${project.version}.jar")
    }
}