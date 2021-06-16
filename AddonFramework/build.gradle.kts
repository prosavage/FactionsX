plugins {
    java
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.16.4-R0.1-SNAPSHOT")
    compileOnly("net.prosavage:BasePlugin:1.7.6")
}

tasks {

    compileJava {
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
    }

    getByName<Jar>("jar") {
        archiveFileName.set("AddonFramework-${project.version}.jar")
    }
}