package net.prosavage.factionsx.manager

import net.prosavage.factionsx.FactionsX
import net.prosavage.factionsx.persist.IConfigFile
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.persist.config.*
import net.prosavage.factionsx.persist.config.gui.AccessGUIConfig
import net.prosavage.factionsx.persist.config.gui.PermsGUIConfig
import net.prosavage.factionsx.persist.config.gui.UpgradesGUIConfig

object ConfigFileManager {

    fun setup() {
        register(Config)
        register(EconConfig)
        register(RoleConfig)
        register(PermsGUIConfig)
        register(AccessGUIConfig)
        register(ProtectionConfig)
        register(UpgradesConfig)
        register(UpgradesGUIConfig)
        register(MapConfig)
        register(FlyConfig)
        register(ScoreboardConfig)
        register(Message)
    }

    val files = hashSetOf<IConfigFile>()

    fun register(configurableFile: IConfigFile) {
        files.add(configurableFile)
    }

    fun load() {
        files.forEach { it.load(FactionsX.instance) }
    }

    fun save() {
        files.forEach {
            // Load changes first, then save.
            it.load(FactionsX.instance)
            it.save(FactionsX.instance)
        }
    }


}