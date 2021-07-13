package net.prosavage.factionsx.persist

import com.cryptomorin.xseries.XMaterial
import net.prosavage.factionsx.addonframework.AddonPlugin
import net.prosavage.factionsx.struct.ShopItem
import net.prosavage.factionsx.util.SerializableItem
import java.io.File

object ShopConfig {

    @Transient
    private val instance = this


    var guiRows = 3
    var guiTitle = "&7Faction Shop"

    var guiBackgroundItem = SerializableItem(
            XMaterial.BLACK_STAINED_GLASS_PANE,
            "&9 ",
            listOf(""),
            1
    )





    var guiShopItems = hashSetOf<ShopItem>(
            ShopItem(
                    SerializableItem(
                            XMaterial.BEACON,
                            "&7Universal Collector",
                            listOf(
                                    "&b- &7Collects all drops in a chunk.",
                                    "&b- &7Can sort items into paged gui.",
                                    "&b - &7One click sell interface.",
                                    "",
                                    "&bPrice: 100 credits",
                                    "&7&oFrom CollectorsX Plugin."
                            ),
                            1
                    ),
                    1,
                    2,
                    100,
                    listOf(
                            "cx give ALL {player}"
                    )
            ),
            ShopItem(
                    SerializableItem(
                            XMaterial.ENCHANTED_GOLDEN_APPLE,
                            "&eGolden Apple",
                            listOf(
                                    "&e- &7Basically makes you invincible.",
                                    "",
                                    "&bPrice: 10 credits"
                            ),
                            1
                    ),
                    1,
                    4,
                    10,
                    listOf(
                            "give {player} enchanted_golden_apple"
                    )
            ),
            ShopItem(
                    SerializableItem(
                            XMaterial.DIAMOND,
                            "&bA Cool Diamond",
                            listOf(
                                    "&b- &7Just an example item.",
                                    "",
                                    "&bPrice: 1,000 credits"
                            ),
                            1
                    ),
                    1,
                    6,
                    1000,
                    listOf(
                            "give {player} diamond"
                    )
            )
    )

    var cmdShopSuccess = "&7Purchased for &6%1\$s&7 credits."
    var cmdShopNotEnough = "&7You dont have enough credits for that item."
    var cmdShopHelp = "&7open faction shop."

    fun save(addon: AddonPlugin) {
        addon.configSerializer.save(instance, File(addon.dataFolder, "config.json"))
    }

    fun load(addon: AddonPlugin) {
        addon.configSerializer.load(instance, ShopConfig::class.java, File(addon.dataFolder, "config.json"))
    }

}