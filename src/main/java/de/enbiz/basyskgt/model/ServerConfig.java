package de.enbiz.basyskgt.model;

public class ServerConfig {

    private final String registryPath;

    private final String aasServerPath;

    public ServerConfig(String registryPath, String aasServerPath) {
        this.registryPath = registryPath;
        this.aasServerPath = aasServerPath;
    }

    public String getRegistryPath() {
        return registryPath;
    }

    public String getAasServerPath() {
        return aasServerPath;
    }
}
