package net.prosavage.factionsx.addonframework;

/**
 * This class holds data about a startup response.
 */
public class StartupResponse {
    /**
     * {@link Boolean} whether or not the startup was successful.
     */
    public final boolean success;

    /**
     * {@link String} possible error that occurred during startup operation(s).
     */
    public final String possibleError;

    /**
     * @param possibleError {@link String} possible error that occurred during startup operation(s).
     */
    private StartupResponse(String possibleError) {
        this.success = false;
        this.possibleError = possibleError;
    }

    /**
     * No-arg constructor used to define "true"
     */
    private StartupResponse() {
        this.success = true;
        this.possibleError = null;
    }

    /**
     * Create a new error instance of startup with a designated message.
     *
     * @param message {@link String} message to be provided.
     * @return {@link StartupResponse}
     */
    public static StartupResponse error(String message) {
        return new StartupResponse(message);
    }

    /**
     * Create a new successful startup response.
     *
     * @return {@link StartupResponse}
     */
    public static StartupResponse ok() {
        return new StartupResponse();
    }
}