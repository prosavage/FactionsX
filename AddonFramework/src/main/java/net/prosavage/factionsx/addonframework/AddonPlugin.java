package net.prosavage.factionsx.addonframework;

import net.prosavage.baseplugin.serializer.Serializer;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import static org.bukkit.Bukkit.getPluginManager;
import static org.bukkit.ChatColor.translateAlternateColorCodes;

/**
 * The abstraction layer for FactionX addons.
 */
public abstract class AddonPlugin extends JavaPlugin {
    /**
     * {@link ConsoleCommandSender} the console command sender to be used for various logs.
     */
    private static final ConsoleCommandSender CONSOLE_SENDER = Bukkit.getConsoleSender();

    /**
     * {@link Serializer} the data serializer instance.
     */
    public final Serializer dataSerializer = new Serializer(true, getDataFolder(), Logger.getGlobal());

    /**
     * {@link Serializer} the configuration serializer instance.
     */
    public final Serializer configSerializer = new Serializer(false, getDataFolder(), Logger.getGlobal());

    /**
     * {@link Boolean} whether or not to create the parent folder.
     */
    private final boolean createParentFolder;

    /**
     * Primary constructor;
     *
     * @param createParentFolder {@link Boolean} whether or not to create the parent folder.
     */
    public AddonPlugin(boolean createParentFolder) {
        this.createParentFolder = createParentFolder;
    }

    /**
     * This method contains all plugin enabling functionality.
     *
     * @return {@link StartupResponse}
     */
    protected abstract StartupResponse onStart();

    /**
     * This method contains all plugin disabling functionality.
     */
    protected abstract void onTerminate();

    /**
     * Called when the plugin is enabled.
     */
    @Override
    public void onEnable() {
        // make sure fx is present
        if (!getPluginManager().isPluginEnabled("FactionsX")) {
            logColored("Failed to enable: FactionsX is not running.");
            getPluginManager().disablePlugin(this);
            return;
        }

        // create parent folder if wanted
        if (this.createParentFolder) {
            final boolean created = this.getDataFolder().mkdir();
            if (created) logColored("Configuration folder was created.");
        }

        // attempt startup
        final StartupResponse response = this.onStart();
        if (!response.success) {
            logColored(String.format("Failed to enable: %s", response.possibleError));
            getPluginManager().disablePlugin(this);
            return;
        }

        // success
        logColored("Successfully enabled.");
    }

    /**
     * Called when the plugin is disabled.
     */
    @Override
    public void onDisable() {
        this.onTerminate();
    }

    /**
     * Log a colored message to the console.
     *
     * @param line {@link String} the line to be logged.
     */
    public void logColored(String line) {
        CONSOLE_SENDER.sendMessage(translateAlternateColorCodes(
            '&', "&7[&6FactionsX-&a" + getName() + "&7] &r" + line
        ));
    }
}