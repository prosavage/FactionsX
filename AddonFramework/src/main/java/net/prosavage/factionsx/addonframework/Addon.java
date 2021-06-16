package net.prosavage.factionsx.addonframework;


import net.prosavage.baseplugin.serializer.Serializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public abstract class Addon {


    private boolean enabled;
    private Serializer dataSerializer;
    private Serializer configSerializer;
    private JavaPlugin instance;
    private String name;
    private AddonClassloader classLoader;
    private File addonDataFolder;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public JavaPlugin getFactionsXInstance() {
        return instance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AddonClassloader getClassLoader() {
        return classLoader;
    }

    public void setClassLoader(AddonClassloader classLoader) {
        this.classLoader = classLoader;
    }

    public File getAddonDataFolder() {
        return addonDataFolder;
    }

    void enable(JavaPlugin instance, File addonDataFolder, Serializer dataSerializer, Serializer configSerializer) {
        if (this.isEnabled()) {
            throw new IllegalStateException("Cannot enable the addon when it's already enabled");
        }
        this.instance = instance;
        this.addonDataFolder = addonDataFolder;
        this.dataSerializer = dataSerializer;
        this.configSerializer = configSerializer;
        addonDataFolder.mkdirs();
        this.onEnable();
        this.setEnabled(true);
    }

    void disable() {
        if (!this.isEnabled()) {
            throw new IllegalStateException("Cannot disable the addon when it isn't enabled");
        }
        this.onDisable();
        this.setEnabled(false);
    }

    public void logColored(String line) {
        Bukkit.getConsoleSender().sendMessage(
                ChatColor.translateAlternateColorCodes('&', "&7[&6FactionsX-&a" + getName() + "&7] &r" + line));
    }

    /**
     * Called when the addon is enabled
     */
    protected abstract void onEnable();

    /**
     * Called when the addon is disabled
     */
    protected abstract void onDisable();

    public Serializer getDataSerializer() {
        return dataSerializer;
    }

    public Serializer getConfigSerializer() {
        return configSerializer;
    }
}
