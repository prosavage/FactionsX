import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    java
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
}

description = "FactionsX Core Module."

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.1")
    implementation(kotlin("stdlib-jdk8"))
    implementation(project(":AddonFramework"))
    implementation("net.prosavage:BasePlugin:1.9.1") // update when deployed
    implementation("me.rayzr522:jsonmessage:1.2.1")
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("com.github.cryptomorin:XSeries:8.1.0")
    implementation("org.bstats:bstats-bukkit:1.7")
    implementation("io.papermc:paperlib:1.0.2")
    implementation("fr.mrmicky:FastParticles:v2.0.0")
    implementation("com.github.MinusKube:SmartInvs:master-SNAPSHOT")
    implementation("com.github.officialrarlab:FastBoard:b6887c9a5f")
    implementation("com.github.officialrarlab:WorldGuardWrapper:1.0")
    implementation("me.oliwer:BossBarAV:1.1")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("net.ess3:EssentialsX:2.17.2")
    compileOnly("org.spigotmc:spigot-api:1.17-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.10.9")
    compileOnly("com.github.brcdev-minecraft:shopgui-api:1.5.0")
    compileOnly("com.github.MyzelYam:SuperVanish:6.2.0") {
        exclude("be.maximvdw")
        exclude("com.comphenix.protocol")
        exclude("net.citizensnpcs")
    }
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.0")
    compileOnly(files("./lib/FeatherBoard-4.27.0.jar"))
    compileOnly("com.SirBlobman.combatlogx:CombatLogX-API:10.0.0.0-SNAPSHOT")
}


tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }

    jar {
        archiveFileName.set("${project.name}-lib.jar")
    }

    processResources {
        filter<ReplaceTokens>(
                "tokens" to mapOf(
                        "project.version" to project.version
                )
        )
    }

    val shadowJar = named<ShadowJar>("shadowJar") {
        mergeServiceFiles()
        minimize()

        val shadePath = "net.prosavage.factionsx.shade"
        relocate("kotlinx", "$shadePath.kotlinx")
        relocate("kotlin", "$shadePath.kotlin")
        relocate("javax.annotation", "$shadePath.annotations")
        relocate("com.google.gson", "$shadePath.gson")
        relocate("com.cryptomorin.xseries", "$shadePath.xseries")
        relocate("fr.minuskube.inv", "$shadePath.smart-invs")
        relocate("fr.mrmicky.fastboard", "$shadePath.fastboard")
        relocate("fr.mrmicky.fastparticle", "$shadePath.fastparticle")
        relocate("io.papermc.lib", "$shadePath.paperlib")
        relocate("me.rayzr522.jsonmessage", "$shadePath.jsonmessage")
        relocate("org.bstats.bukkit", "$shadePath.bstats-bukkit")
        relocate("org.jetbrains.annotations", "$shadePath.jetbrains-annotations")
        archiveFileName.set("${project.name}-${project.version}.jar")
    }
}