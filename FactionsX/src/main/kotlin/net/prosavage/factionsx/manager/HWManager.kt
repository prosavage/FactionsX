package net.prosavage.factionsx.manager

//import net.prosavage.factionsx.util.logColored
//import oshi.SystemInfo
//
//object HWManager {
//
//    val systemInfo = SystemInfo()
//    val hardware = systemInfo.hardware
//
//
//    fun printInfo() {
//        val stores = hardware.diskStores
//        val memory = hardware.memory
//        logColored("Running on: ${hardware.processor.name}")
//        val totalMemory = memory.total
//        logColored("Memory: ${(totalMemory - memory.available).toMb()}MB/${totalMemory.toMb()}MB")
////        Arrays.stream(stores)
////    }    .forEach { e: HWDiskStore -> logColored(e.name + ":" + e.serial)
//    }
//
//    private fun Long.toMb(): Long {
//        return this / (1024L * 1024L)
//    }
//
//
//}