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
    compileOnly(kotlin("stdlib-jdk8"))
    compileOnly("com.github.cryptomorin:XSeries:7.2.1")
    compileOnly ("org.spigotmc:spigot-api:1.16.1-R0.1-SNAPSHOT")
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
        relocate("com.cryptomorin.xseries", "$shadePath.xseries")
        relocate("net.prosavage.factionsx.addonframework", "$shadePath.addonframework")
        archiveFileName.set("FRoster-Addon-${project.version}.jar")
        println("Compiled FRoster-Addon")
    }
}