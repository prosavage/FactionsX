allprojects {
    group = "net.prosavage.factionsx"
    version = "1.0.6-RC"
}

plugins {
    java
    kotlin("jvm") version "1.3.72"
    id("com.github.johnrengelman.shadow") version "5.1.0"
}

subprojects {
    repositories {
        mavenLocal()
        mavenCentral()

        maven("https://repo.maven.apache.org/maven2")

        maven("https://nexus.savagelabs.net/repository/maven-releases/")

        maven("https://hub.spigotmc.org/nexus/content/groups/public/")

        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots")

        maven("https://repo.dmulloy2.net/nexus/repository/public/")
        maven("https://oss.sonatype.org/content/groups/public/")

        maven("http://repo.extendedclip.com/content/repositories/placeholderapi/")

        maven("https://repo.codemc.org/repository/maven-public")

        maven("https://papermc.io/repo/repository/maven-public/")


        maven("https://ci.ender.zone/plugin/repository/everything/")

//        maven("https://mavenrepo.cubekrowd.net/artifactory/repo/")

        maven("https://maven.enginehub.org/repo/")

        maven("https://jcenter.bintray.com/")

//        maven("https://nexus.savagelabs.net/repository/maven-public/")

        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")

        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("http://repo.mvdw-software.be/content/groups/public/")
        maven("https://papermc.io/repo/repository/maven-releases/")
        maven("https://ci.ender.zone/plugin/repository/everything/")
        maven("https://minevolt.net/repo/")

        maven("https://jitpack.io")
    }
}

repositories {
    mavenCentral()
    mavenLocal()
}