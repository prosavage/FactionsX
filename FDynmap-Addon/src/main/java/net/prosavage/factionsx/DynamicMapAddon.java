package net.prosavage.factionsx;

import net.prosavage.factionsx.addonframework.Addon;
import net.prosavage.factionsx.command.MapColorCommand;
import net.prosavage.factionsx.command.engine.FCommand;
import net.prosavage.factionsx.listener.GlobalMapListener;
import net.prosavage.factionsx.map.MapBase;
import net.prosavage.factionsx.persistence.DynamicConfig;
import net.prosavage.factionsx.persistence.DynamicData;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.dynmap.DynmapAPI;
import org.jetbrains.annotations.NotNull;

import static net.prosavage.factionsx.FactionsX.baseCommand;
import static net.prosavage.factionsx.helper.ConfigurationCleanup.loadBySerializer;
import static net.prosavage.factionsx.helper.ConfigurationCleanup.saveBySerializer;
import static org.bukkit.Bukkit.getPluginManager;

public final class DynamicMapAddon extends Addon {
    /**
     * {@link Listener} instance of the global map listener.
     */
    private Listener globalMapListener;

    /**
     * {@link MapBase} instance of the map base to access related functionality.
     */
    private MapBase mapBase;

    /**
     * {@link FCommand} instance of the color change command.
     */
    private FCommand changeCommand;

    /**
     * Handle plugin enabling operations here.
     **/
    @Override
    protected void onEnable() {
        logColored("Enabling dynmap addon");

        // make sure that the server has got dynmap running
        if (!MapBase.attemptRegistration()) {
            this.setEnabled(false);
            return;
        }

        // load our config and data
        loadBySerializer(this, false, DynamicConfig.class);
        loadBySerializer(this, true, DynamicData.class);

        // our mapbase marking
        mapBase = new MapBase(this.fetchApi());
        mapBase.callAsync(mapBase::markAll);

        // register command
        changeCommand = new MapColorCommand(mapBase);
        baseCommand.addSubCommand(changeCommand);

        // initialize our listeners
        this.globalMapListener = new GlobalMapListener(mapBase);

        // register listeners
        final PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(globalMapListener, FactionsX.instance);
    }

    /**
     * Handle plugin disabling operations here.
     **/
    @Override
    protected void onDisable() {
        // save our config and data
        saveBySerializer(this, false, DynamicConfig.class);
        saveBySerializer(this, true, DynamicData.class);

        // unregister command
        if (changeCommand != null) baseCommand.removeSubCommand(changeCommand);
        changeCommand = null;

        // unregister listeners
        if (globalMapListener != null) HandlerList.unregisterAll(globalMapListener);
        globalMapListener = null;

        // unmark all claims
        if (mapBase != null) {
            // remove all markers
            mapBase.callAsync(mapBase::unmarkAll);
            mapBase = null;
        }
    }

    /**
     * Fetch the dynamic map's API.
     *
     * @return {@link DynmapAPI} corresponding API instance.
     */
    @NotNull
    private DynmapAPI fetchApi() {
        return (DynmapAPI) getPluginManager().getPlugin("dynmap");
    }
}