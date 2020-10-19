allprojects {
    group = "net.prosavage.factionsx"
    version = "1.0.7-RC"
}

plugins {
    java
    kotlin("jvm") version "1.3.72"
    id("com.github.johnrengelman.shadow") version "5.1.0"
}

val serverPluginDirectory: String by project

tasks {

    register("setCIVersion") {

        if (hasProperty("teamcity")) {
            version = "dev-#%build.counter%}"
        } else {
            println("Not running in teamcity env, version will not be changed.")
        }
    }


    register("copyToServer") {
        doLast {
            // Copy all our stuff to root project libs.
            for (subproject in subprojects.filter { project -> project.name != "AddonFramework" }) {
                println("Copying ${subproject.name}")
                copy {
                    from("${subproject.buildDir}/libs/")
                    into("${buildDir}/libs/")
                    include("${subproject.name}-${version}.jar")
                }
            }

            if (project.hasProperty("serverPluginDirectory").not()) {
                println("No serverPluginDirectory argument found, ex: \n" +
                        "gradle shadowJar copyToServer -PserverPluginDirectory=~/Documents/mc-server/plugins/")
                return@doLast
            }
            println("copying $serverPluginDirectory")
            // Copy the plugins.
            copy {
                from("$buildDir/libs/")
                into(serverPluginDirectory)
                include("*.jar")
                exclude("${project.name}-$version-all.jar", "*-Addon-$version.jar")
            }

            // Copy the addons.
            copy {
                from("$buildDir/libs/")
                into("$serverPluginDirectory/FactionsX/addons")
                include("*-Addon-$version.jar")
                exclude("${project.name}-$version-all.jar")
            }
        }
    }
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