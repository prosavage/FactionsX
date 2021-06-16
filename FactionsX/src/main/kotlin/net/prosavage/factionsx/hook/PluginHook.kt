package net.prosavage.factionsx.hook

interface PluginHook {

    val pluginName: String

    fun load()

    fun isHooked(): Boolean

}