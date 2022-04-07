package de.enbiz.basyskgt.model;

public class RegistrationStatus {

    private final boolean registeredToAasRegistry;

    private final boolean shellUploadedToRepository;

    private final boolean registryReachable;

    private final boolean aasServerReachable;

    public RegistrationStatus(boolean registeredToAasRegistry, boolean shellUploadedToRepository, boolean registryReachable, boolean aasServerReachable) {
        this.registeredToAasRegistry = registeredToAasRegistry;
        this.shellUploadedToRepository = shellUploadedToRepository;
        this.registryReachable = registryReachable;
        this.aasServerReachable = aasServerReachable;
    }

    public boolean isRegisteredToAasRegistry() {
        return registeredToAasRegistry;
    }

    public boolean isShellUploadedToRepository() {
        return shellUploadedToRepository;
    }

    public boolean isRegistryReachable() {
        return registryReachable;
    }

    public boolean isAasServerReachable() {
        return aasServerReachable;
    }
}
