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
    compileOnly(kotlin("stdlib-jdk8"))
    compileOnly("org.spigotmc:spigot-api:1.17-R0.1-SNAPSHOT")
    compileOnly("com.github.MinusKube:SmartInvs:master-SNAPSHOT")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.1")
    compileOnly("com.github.SavageLabs:JSONMessage:410f38c")
    compileOnly("me.clip:placeholderapi:2.10.9")
    compileOnly("com.github.cryptomorin:XSeries:8.1.0")
    compileOnly("com.github.OmerBenGera:WildStackerAPI:b19")
}


tasks {
    val shadowJar = named<ShadowJar>("shadowJar") {
        mergeServiceFiles()
        val shadePath = "net.prosavage.factionsx.shade"
        relocate("kotlinx", "$shadePath.ftop-addon.kotlinx")
        relocate("kotlin", "$shadePath.kotlin")
        relocate("javax.annotation", "$shadePath.annotations")
        relocate("com.cryptomorin.xseries", "$shadePath.xseries")
        relocate("fr.minuskube.inv", "$shadePath.smart-invs")
        relocate("io.papermc.lib", "$shadePath.paperlib")
        relocate("me.rayzr522.jsonmessage", "$shadePath.jsonmessage")
        minimize()
        archiveFileName.set("FTOP-Addon-${project.version}.jar")
    }
}