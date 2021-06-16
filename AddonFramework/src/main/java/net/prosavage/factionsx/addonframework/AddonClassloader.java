package net.prosavage.factionsx.addonframework;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AddonClassloader extends URLClassLoader {

    private final AddonManager addOnManager;
    private final Map<String, Class<?>> classes = new ConcurrentHashMap<>();
    private final Addon addOn;
    private final File file;
    private final String name;
    private boolean disabling;

    AddonClassloader(final AddonManager addOnManager,
                     final File file,
                     final String name) throws AddOnLoaderException, MalformedURLException {
        super(new URL[]{file.toURI().toURL()}, addOnManager.getClass().getClassLoader());

        this.addOnManager = addOnManager;
        this.file = file;
        this.name = name;
        this.addOn = null; // This is a library

        if (!file.toPath().getFileName().toString().equalsIgnoreCase(name)) {
            throw new AddOnLoaderException("File name does not match addon name...");
        }
    }

    AddonClassloader(final AddonManager addOnManager,
                     final File file,
                     final String mainFile,
                     final String name) throws AddOnLoaderException, MalformedURLException {
        super(new URL[]{file.toURI().toURL()}, addOnManager.getClass().getClassLoader());

        this.addOnManager = addOnManager;
        this.file = file;

        Class<?> mainClass;
        try {
            mainClass = Class.forName(mainFile, true, this);
            this.classes.put(mainClass.getName(), mainClass);
        } catch (final ClassNotFoundException e) {
            throw new AddOnLoaderException("Could not find main class for addOn " + name);
        }
        Class<? extends Addon> addOnMain;
        try {
            addOnMain = mainClass.asSubclass(Addon.class);
        } catch (final Exception e) {
            throw new AddOnLoaderException(mainFile + " does not implement AddOn");
        }
        try {
            this.addOn = addOnMain.newInstance();
            this.addOn.setClassLoader(this);
            this.addOn.setName(name);
            this.name = name;
        } catch (final Exception e) {
            throw new AddOnLoaderException("Failed to load main class for " + name, e);
        }
    }

    public AddonManager getAddOnManager() {
        return this.addOnManager;
    }

    public Addon getAddOn() {
        return addOn;
    }

    public File getFile() {
        return file;
    }

    public String getName() {
        return name;
    }

    public boolean isDisabling() {
        return this.disabling;
    }

    public void setDisabling(boolean disabling) {
        this.disabling = disabling;
    }

    @Override
    protected Class<?> findClass(final String name) throws ClassNotFoundException {
        return findClass(name, true);
    }

    Class<?> findClass(final String name, final boolean global) throws ClassNotFoundException {
        if (this.isDisabling()) {
            throw new ClassNotFoundException("This class loader is disabling...");
        }
        if (classes.containsKey(name)) {
            return classes.get(name);
        } else {
            Class<?> clazz = null;
            if (global) {
                clazz = addOnManager.findClass(name);
            }
            if (clazz == null) {
                clazz = super.findClass(name);
                if (clazz != null) {
                    addOnManager.setClass(name, clazz);
                }
            }
            return clazz;
        }
    }

    void removeClasses() {
        if (!this.isDisabling()) {
            throw new IllegalStateException("Cannot remove class when the loader isn't disabling...");
        }
        this.addOnManager.removeAll(this.classes);
        this.classes.clear();
    }

    private static class AddOnLoaderException extends RuntimeException {

        private AddOnLoaderException(String error) {
            super(error);
        }

        private AddOnLoaderException(String error, Throwable cause) {
            super(error, cause);
        }

    }


}
