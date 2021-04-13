package net.prosavage.factions

import net.prosavage.factions.persist.CoreChunkStore
import net.prosavage.factionsx.addonframework.Addon

class FCoreChunksAddon : Addon() {
    override fun onEnable() {
        loadFiles()
    }

    override fun onDisable() {
        saveFiles()
    }


    private fun loadFiles() {
        CoreChunkStore.load(this);
    }

    private fun saveFiles() {
        CoreChunkStore.save(this);
    }
}