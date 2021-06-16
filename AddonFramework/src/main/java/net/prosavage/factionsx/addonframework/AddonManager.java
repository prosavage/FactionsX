package net.prosavage.factionsx.addonframework;

import net.prosavage.baseplugin.serializer.Serializer;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public class AddonManager {

    private final JavaPlugin instance;

    private final File addOnFolder;

    private final Serializer dataSerializer;

    private final Serializer configSerializer;
    private Map<String, Class> globalClassMap = new ConcurrentHashMap<>();
    private Map<String, AddonClassloader> classLoaders = new ConcurrentHashMap<>();

    public AddonManager(JavaPlugin instance, Serializer dataSerializer, Serializer configSerializer) {
        this.instance = instance;
        this.addOnFolder = new File(instance.getDataFolder(), "addons");
        this.addOnFolder.mkdirs();
        this.dataSerializer = dataSerializer;
        this.configSerializer = configSerializer;
    }

    public File getAddOnFolder() {
        return this.addOnFolder;
    }

    /**
     * Load all addons in the specified addon folder
     *
     * @throws AddOnManagerException If the loading of the addons fails for any reason
     */
    @SuppressWarnings("WeakerAccess")
    public void load() throws AddOnManagerException {
        if (!addOnFolder.exists()) {
            if (!addOnFolder.mkdir()) {
                throw new AddOnManagerException("Couldn't create AddOn folder");
            }
        }
        final File[] files = addOnFolder.listFiles(new JarFilter());
        if (files == null) {
            return;
        }
        final List<File> fileList = Arrays.asList(files);
        //
        // This makes sure that libraries are loaded before addons
        //
        fileList.stream().filter(this::isLibrary).forEach(this::loadAddon);
        fileList.stream().filter(file -> !this.isLibrary(file)).forEach(this::loadAddon);
    }

    private Properties getAddOnProperties(final File file) throws AddOnManagerException {
        final JarFile jar;
        try {
            jar = new JarFile(file);
        } catch (final IOException e) {
            throw new AddOnManagerException("Failed to create jar object from " + file.getName(), e);
        }
        final JarEntry desc = jar.getJarEntry("addon.properties");
        if (desc == null) {
            return null;
        }
        final Properties properties = new Properties();
        try (final InputStream stream = jar.getInputStream(desc)) {
            properties.load(stream);
        } catch (final Exception e) {
            throw new AddOnManagerException("Failed to load \"addon.properties\" in " + file.getName());
        }
        return properties;
    }

    void setClass(final String name,
                  Class<?> clazz) {
        this.globalClassMap.put(name, clazz);
    }

    Class<?> findClass(final String name) {
        if (this.globalClassMap.containsKey(name)) {
            return this.globalClassMap.get(name);
        }
        Class<?> clazz;
        for (final AddonClassloader loader : this.classLoaders.values()) {
            try {
                if ((clazz = loader.findClass(name, false)) != null) {
                    this.globalClassMap.put(name, clazz);
                    return clazz;
                }
            } catch (final Exception ignored) {
            }
        }
        return null;
    }

    /**
     * Get an immutable copy of the underlying library list
     *
     * @return All loaded libraries
     */
    @SuppressWarnings("WeakerAccess")
    public Collection<String> getLibraries() {
        return Collections.unmodifiableList(this.classLoaders.values().stream()
                .filter(loader -> loader.getAddOn() == null).map(AddonClassloader::getName)
                .collect(Collectors.toList()));
    }

    /**
     * Get an immutable copy of the underlying addon list
     *
     * @return All loaded addons
     */
    @SuppressWarnings("WeakerAccess")
    public Collection<Addon> getAddOns() {
        return Collections.unmodifiableList(this.classLoaders.values().stream()
                .filter(loader -> loader.getAddOn() != null).map(AddonClassloader::getAddOn)
                .collect(Collectors.toList()));
    }

    /**
     * Get the instance of an addon, if it exists
     *
     * @param clazz Addon class
     * @return Instance, if it can be found
     */
    @SuppressWarnings("WeakerAccess")
    public <T extends Addon> Optional<T> getAddOnInstance(final Class<T> clazz) {
        return this.classLoaders.values().stream().filter(loader -> loader.getAddOn() != null).
                map(AddonClassloader::getAddOn).filter(addOn -> addOn.getClass().equals(clazz))
                .map(clazz::cast).findAny();
    }

    @SuppressWarnings("WeakerAccess")
    public Optional<Addon> getAddOnInstance(final String addOnName) {
        if (this.classLoaders.containsKey(addOnName)) {
            return Optional.of(this.classLoaders.get(addOnName).getAddOn());
        }
        return Optional.empty();
    }

    /**
     * Unload an addon
     *
     * @param clazz Addon class
     */
    @SuppressWarnings("WeakerAccess")
    public <T extends Addon> void unloadAddon(final Class<T> clazz) {
        getAddOnInstance(clazz).ifPresent(this::unloadAddon);
    }

    /**
     * Unload an addon
     *
     * @param addOn Addon instance
     * @throws AddOnManagerException If anything happens during the unloading of the addon
     */
    @SuppressWarnings("WeakerAccess")
    public <T extends Addon> void unloadAddon(final T addOn) throws AddOnManagerException {
        if (addOn.isEnabled()) {
            addOn.disable();
        }
        final AddonClassloader classLoader = addOn.getClassLoader();
        classLoader.setDisabling(true);
        classLoader.removeClasses();
        final String name = addOn.getName();
        if (!this.classLoaders.containsKey(name)) {
            throw new AddOnManagerException("Cannot find class loader by name \"" + name + "\". Panicking!");
        }
        classLoader.setDisabling(false);
        this.classLoaders.remove(name);
    }

    /**
     * Will disable, unload, load and enable the addon
     *
     * @param addOn Addon
     * @return New AddOn instance
     * @throws AddOnManagerException If anything goes wrong
     */
    @SuppressWarnings("WeakerAccess")
    public <T extends Addon> Addon reloadAddon(final T addOn) throws AddOnManagerException {
        if (addOn.isEnabled()) {
            addOn.disable();
        }
        final File file = addOn.getClassLoader().getFile();
        this.unloadAddon(addOn);
        final AddonClassloader classLoader = this.loadAddon(file);
        if (classLoader != null && classLoader.getAddOn() != null) {
            classLoader.getAddOn().enable(instance, new File(getAddOnFolder(), addOn.getName()), getDataSerializer(), getConfigSerializer());
        } else {
            throw new AddOnManagerException("Failed to enable addon...");
        }
        return classLoader.getAddOn();
    }

    public void reloadAddOnConfig(final Addon addOn) {
        addOn.onDisable();
        addOn.onEnable();
    }

    private boolean isLibrary(final File file) {
        Properties properties = null;
        try {
            properties = getAddOnProperties(file);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return properties == null;
    }

    /**
     * Load an addon from a jar file
     *
     * @param file AddOn jar file
     * @throws AddOnManagerException If anything goes wrong during the loading
     */
    @SuppressWarnings("WeakerAccess")
    public AddonClassloader loadAddon(final File file) throws AddOnManagerException {
        final Properties properties;
        try {
            properties = getAddOnProperties(file);
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
        if (properties == null) {
            return this.loadLibrary(file);
        } else {
            if (!properties.containsKey("main")) {
                new AddOnManagerException("\"addon.properties\" for " + file.getName() + " has no \"main\" key")
                        .printStackTrace();
                return null;
            }
            if (!properties.containsKey("name")) {
                new AddOnManagerException("\"addon.properties\" for " + file.getName() + " has no \"name\" key")
                        .printStackTrace();
                return null;
            }
            final String addOnName = properties.get("name").toString();
            if (this.classLoaders.containsKey(addOnName)) {
                throw new AddOnManagerException("AddOn of name \"" + addOnName + "\" has already been loaded...");
            }
            final String main = properties.get("main").toString();
            if (this.globalClassMap.containsKey(main)) {
                throw new AddOnManagerException("AddOn main class has already been loaded: " + main);
            }
            final AddonClassloader loader;
            try {
                loader = new AddonClassloader(this, file, main, addOnName);
            } catch (final Exception e) {
                new AddOnManagerException("Failed to load " + file.getName(), e)
                        .printStackTrace();
                return null;
            }
            this.classLoaders.put(addOnName, loader);
            return loader;
        }
    }

    private AddonClassloader loadLibrary(final File file) {
        final AddonClassloader loader;
        try {
            loader = new AddonClassloader(this, file, file.toPath().getFileName().toString());
        } catch (final Exception e) {
            new AddOnManagerException("Failed to load " + file.getName(), e)
                    .printStackTrace();
            return null;
        }
        this.classLoaders.put(loader.getName(), loader);
        return loader;
    }

    /**
     * Enable all addons. Does NOT load any addons.
     */
    @SuppressWarnings("WeakerAccess")
    public void enableAddons() {
        this.classLoaders.values().stream().filter(loader -> loader.getAddOn() != null)
                .map(AddonClassloader::getAddOn).filter(addOn -> !addOn.isEnabled()).forEach(addon -> addon.enable(instance, new File(getAddOnFolder(), addon.getName()), getDataSerializer(), getConfigSerializer()));
    }

    /**
     * Disable all addons. Does NOT unload them.
     */
    @SuppressWarnings("WeakerAccess")
    public void disableAddons() {
        this.classLoaders.values().stream().filter(loader -> loader.getAddOn() != null)
                .map(AddonClassloader::getAddOn).filter(Addon::isEnabled).forEach(Addon::disable);
    }

    void removeAll(final Map<String, Class<?>> clear) {
        clear.keySet().forEach(key -> this.globalClassMap.remove(key));
        for (final Class<?> clazz : clear.values()) {
            if (this.globalClassMap.containsValue(clazz)) {
                throw new AddOnManagerException("Clazz did not get removed...");
            }
        }
    }

    public Serializer getDataSerializer() {
        return dataSerializer;
    }

    public Serializer getConfigSerializer() {
        return configSerializer;
    }

    private static final class AddOnManagerException extends RuntimeException {

        private AddOnManagerException(final String error) {
            super(error);
        }

        private AddOnManagerException(final String error, final Throwable cause) {
            super(error, cause);
        }

    }


}
