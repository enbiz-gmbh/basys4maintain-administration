package de.enbiz.basyskgt.model;

import org.springframework.context.annotation.Configuration;

@Configuration
public class RegistrationStatus {

    private boolean registeredToAasRegistry;

    private boolean shellUploadedToRepository;

    public RegistrationStatus() {
        this.registeredToAasRegistry = false;
        this.shellUploadedToRepository = false;
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
