package net.prosavage.factionsx.persist

import net.prosavage.factionsx.FactionsX

interface IConfigFile {
    fun save(factionsx: FactionsX)
    fun load(factionsx: FactionsX)
}