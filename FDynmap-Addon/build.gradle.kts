import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    id("com.github.johnrengelman.shadow")
}

repositories {
    maven("https://repo.mikeprimm.com/")
}

dependencies {
    compileOnly(project(":FactionsX"))
    compileOnly(project(":AddonFramework"))
    compileOnly("net.prosavage:BasePlugin:1.7.6")
    compileOnly("org.spigotmc:spigot-api:1.17-R0.1-SNAPSHOT")
    implementation("org.jetbrains:annotations:13.0")
    compileOnly("org.dynmap:dynmap:2.0") {
        exclude("org.bukkit")
        exclude("com.nijikokun.bukkit")
        exclude("de.bananaco")
        exclude("org.anjocaido")
        exclude("org.getspout")
        exclude("com.platymuus.bukkit.permissions")
        exclude("ru.tehkode")
    }
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
        archiveFileName.set("FDynmap-Addon-${project.version}.jar")
    }
}