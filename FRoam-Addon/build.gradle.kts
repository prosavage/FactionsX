import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
}

dependencies {
    compileOnly(project(":FactionsX"))
    compileOnly(project(":AddonFramework"))
    compileOnly("net.prosavage:BasePlugin:1.7.6")
    compileOnly("io.papermc:paperlib:1.0.2")
    compileOnly("io.netty:netty-all:4.1.51.Final")
    compileOnly("com.sk89q.worldguard:worldguard-legacy:6.2") // we'll use the oldest implementation cause the packages remain the same, we just need the id
    compileOnly(kotlin("stdlib-jdk8"))
    compileOnly("com.github.cryptomorin:XSeries:8.1.0")
    implementation("com.github.officialrarlab:ClassicPlugin:ea4318b412") {
        exclude("org.jetbrains")
        exclude("org.jetbrains.kotlin")
    }
    compileOnly(files("./lib/spigot-1.16.4.jar"))
    compileOnly("com.github.officialrarlab:WorldGuardWrapper:1.0")
}


tasks {

    compileKotlin {
        kotlinOptions { jvmTarget = "1.8" }
        sourceCompatibility = "1.8"
    }
    val shadowJar = named<ShadowJar>("shadowJar") {
        mergeServiceFiles()
        minimize()
        val shadePath = "net.prosavage.factionsx.shade"
        relocate("kotlin", "$shadePath.kotlin")
        relocate("club.rarlab.classicplugin", "$shadePath.classicplugin")
        relocate("io.papermc.lib", "$shadePath.paperlib")
        relocate("com.cryptomorin.xseries", "$shadePath.xseries")
        archiveFileName.set("FRoam-Addon-${project.version}.jar")
    }
}