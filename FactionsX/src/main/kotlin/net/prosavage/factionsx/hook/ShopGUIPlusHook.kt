package net.prosavage.factionsx.hook

import net.brcdev.shopgui.ShopGuiPlugin
import net.brcdev.shopgui.ShopGuiPlusApi
import net.brcdev.shopgui.shop.ShopItem
import net.prosavage.factionsx.util.logColored
import org.bukkit.Bukkit
import org.bukkit.inventory.ItemStack

object ShopGUIPlusHook : PluginHook {

    override val pluginName: String = "ShopGUIPlus"

    lateinit var shopGUI: ShopGuiPlugin

    override fun isHooked(): Boolean {
        return this::shopGUI.isInitialized
    }

    override fun load() {
        if (Bukkit.getPluginManager().isPluginEnabled(pluginName))
            this.shopGUI = ShopGuiPlusApi.getPlugin() ?: return
        if (isHooked()) logColored("Hooked into $pluginName.")
    }

    fun getShopItem(itemStack: ItemStack): ShopItem? {
        return shopGUI.shopManager.findShopItemByItemStack(itemStack, false)
    }

    fun getBuyPrice(itemStack: ItemStack, defaultPrice: Double): Double? {
        return if (isHooked()) getShopItem(itemStack)?.getBuyPriceForAmount(itemStack.amount) else defaultPrice
    }

    fun getSellPrice(itemStack: ItemStack): Double {
        return if (isHooked()) getShopItem(itemStack)?.getSellPriceForAmount(itemStack.amount) ?: 0.0 else 0.0
    }
}