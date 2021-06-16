package net.prosavage.factionsx.persist.config

import net.prosavage.baseplugin.serializer.Serializer
import net.prosavage.factionsx.FactionsX
import net.prosavage.factionsx.persist.IConfigFile
import java.io.File

object FlyConfig : IConfigFile {

    @Transient
    private val instance = this

    var factionsFly = true
    var factionsFlyParticles = true
    var factionsFlyAutoEnable = true
    var factionsFlyAutoEnableMessage = "Factions Fly has been %1\$s."
    var factionsFlyDisabled = "&7This server has disabled factions fly."
    var factionsFlyIgnoredGamemode = "&7You cannot fly in &6%1\$s&7."
    var factionsFlyNotHere = "&7You cannot fly here."
    var factionsFlyDisabledWorld = "&7Factions fly is disabled in this world."
    var factionsFlyAutoEnableMessageEnable = "enabled"
    var factionsFlyAutoEnableMessageDisable = "disabled"
    var enemyNearByCheck = true
    var factionsFlyEnemyNearby = "&cFactions fly disabled due to enemy nearby."
    var enemyNearByCheckDistance = 20.0
    var particleCount = 30
    var particleParticleOffSetX = 0.3
    var particleParticleOffSetY = 0.1
    var particleParticleOffSetZ = 0.3


    var changeParticlesWhenLookingDown = true
    var changeParticleAtPitchHigherThan = 70
    var lookingDownParticleCount = 3
    var lookingDownParticleOffSetX = 0.05
    var lookingDownParticleOffSetY = 0.1
    var lookingDownParticleOffSetZ = 0.05


    var showSmokeWhenFlyTurnsOff = true
    var smokeParticleCount = 500
    var smokeOffSetX = 1.0
    var smokeOffSetY = 1.0
    var smokeOffSetZ = 1.0


    override fun save(instance: FactionsX) {
        Serializer(false, instance.dataFolder, instance.logger)
                .save(FlyConfig.instance, File("${instance.dataFolder}/config", "fly-config.json"))
    }

    override fun load(factionsx: FactionsX) {
        Serializer(false, factionsx.dataFolder, factionsx.logger)
                .load(FlyConfig.instance, FlyConfig::class.java, File("${factionsx.dataFolder}/config", "fly-config.json"))
    }


}