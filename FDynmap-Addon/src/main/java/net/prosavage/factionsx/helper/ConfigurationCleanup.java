package net.prosavage.factionsx.helper;

import net.prosavage.baseplugin.serializer.Serializer;
import net.prosavage.baseplugin.shade.javax.Nonnull;
import net.prosavage.factionsx.addonframework.AddonPlugin;

import java.io.File;
import java.lang.reflect.Field;
import java.util.function.Function;

/**
 * This is a utility class made to help with configurations based off of SavageFramework.
 */
public final class ConfigurationCleanup {
    // Since this is a utility class, we don't want instantiations.
    private ConfigurationCleanup() {
        throw new IllegalStateException("ConfigurationCleanup instantiation attempt");
    }

    /**
     * Load a configuration by it's class.
     *
     * @param isData {@link Boolean} whether it's a data file.
     * @param configurationClass {@link Class<T>} class to be used for fetching path & instance to load.
     * @param <T> {@link T} type of configuration.
     */
    public static <T> void loadBySerializer(
            final AddonPlugin addon,
            final boolean isData,
            @Nonnull final Class<T> configurationClass
    ) {
        final Serializer serializer = isData ? addon.dataSerializer : addon.configSerializer;
        serializer.load(getInstance(configurationClass), configurationClass, getPath(addon, configurationClass));
    }

    /**
     * Save a configuration by it's class.
     *
     * @param isData {@link Boolean} whether it's a data file.
     * @param configurationClass {@link Class<T>} class to be used for fetching path & instance to save.
     * @param <T> {@link T} type of configuration.
     */
    public static <T> void saveBySerializer(
            final AddonPlugin addon,
            final boolean isData,
            @Nonnull final Class<T> configurationClass
    ) {
        final Serializer serializer = isData ? addon.dataSerializer : addon.configSerializer;
        serializer.save(getInstance(configurationClass), getPath(addon, configurationClass));
    }

    /**
     * Get the instance of a configuration class.
     *
     * @param configurationClass {@link Class<T>} to fetch the instance from.
     * @param <T> {@link T} solid configuration type to return.
     * @return {@link T}
     */
    @SuppressWarnings("unchecked")
    private static <T> T getInstance(final Class<T> configurationClass) {
        try {
            final Field field = configurationClass.getDeclaredField("INSTANCE");
            field.setAccessible(true);
            return (T) field.get(null);
        } catch (final Exception ex) {
            throw new IllegalStateException(String.format(
                    "Configuration '%s' does not have an INSTANCE field or was illegally accessed.",
                    configurationClass.getSimpleName()
            ), ex);
        }
    }

    /**
     * Get the path of a configuration class.
     *
     * @param addon {@link AddonPlugin} instance to be used to fetch the corresponding data folder.
     * @param configurationClass {@link Class<T>} class used to fetch the PATH field from.
     * @param <T> {@link T} type of configuration.
     * @return {@link File}
     */
    @SuppressWarnings("unchecked")
    private static <T> File getPath(final AddonPlugin addon, final Class<T> configurationClass) {
        try {
            final Field field = configurationClass.getDeclaredField("PATH");
            field.setAccessible(true);
            return ((Function<File, File>) field.get(null)).apply(addon.getDataFolder());
        } catch (final Exception ex) {
            throw new IllegalStateException(String.format(
                    "Configuration '%s' does not have a PATH field or was illegally accessed.",
                    configurationClass.getSimpleName()
            ), ex);
        }
    }
}