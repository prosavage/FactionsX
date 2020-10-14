import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
}

description = "Allows FactionsUUID Hooks to proxy into factionsx."

dependencies {
    compileOnly(project(":FactionsX"))
    compileOnly("net.prosavage:BasePlugin:1.7.6")
    compileOnly("io.papermc:paperlib:1.0.2")
    compileOnly("org.spigotmc:spigot-api:1.16.1-R0.1-SNAPSHOT")
    compileOnly("com.github.cryptomorin:XSeries:7.2.1")
    compileOnly(kotlin("stdlib-jdk8"))
}



tasks {

    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }

    val shadowJar = named<ShadowJar>("shadowJar") {
        mergeServiceFiles()
        minimize()

        val shadePath = "net.prosavage.factionsx.shade"
        relocate("kotlin", "$shadePath.kotlin")
        relocate("io.papermc.lib", "$shadePath.paperlib")
        relocate("com.cryptomorin.xseries", "$shadePath.xseries")
        relocate("net.prosavage.factionsx.addonframework", "$shadePath.addonframework")
        archiveFileName.set("FactionsUUIDAPIInjector-${project.version}.jar")
        println("Compiled FactionsUUIDAPIInjector")
    }
}