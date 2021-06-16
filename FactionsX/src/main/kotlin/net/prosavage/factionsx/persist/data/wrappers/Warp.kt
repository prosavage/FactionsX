package net.prosavage.factionsx.persist.data.wrappers

data class Warp(val name: String, val password: String?, val dataLocation: DataLocation) {
    fun hasPassword(): Boolean {
        return password != null
    }
}


