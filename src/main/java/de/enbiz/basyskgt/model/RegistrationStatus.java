package de.enbiz.basyskgt.model;

/**
 * Singleton class holding info on whether the AAS is currently registered and / or uploaded to the server.
 * Use {@link RegistrationStatus#getInstance()} to get the singleton instance.
 */
public class RegistrationStatus {

    private static RegistrationStatus instance;

    private boolean registeredToAasRegistry;

    private boolean shellUploadedToRepository;

    private RegistrationStatus() {
        this.registeredToAasRegistry = false;
        this.shellUploadedToRepository = false;
    }

    public static RegistrationStatus getInstance() {
        if (instance == null) {
            instance = new RegistrationStatus();
        }
        return instance;
    }

    public boolean isRegisteredToAasRegistry() {
        return registeredToAasRegistry;
    }

    public boolean isShellUploadedToRepository() {
        return shellUploadedToRepository;
    }

    public void setRegisteredToAasRegistry(boolean registeredToAasRegistry) {
        this.registeredToAasRegistry = registeredToAasRegistry;
    }

    public void setShellUploadedToRepository(boolean shellUploadedToRepository) {
        this.shellUploadedToRepository = shellUploadedToRepository;
    }
}
