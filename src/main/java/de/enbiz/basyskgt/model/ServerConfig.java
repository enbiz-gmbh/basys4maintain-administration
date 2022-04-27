package de.enbiz.basyskgt.model;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServerConfig that = (ServerConfig) o;
        return Objects.equals(registryPath, that.registryPath) && Objects.equals(aasServerPath, that.aasServerPath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(registryPath, aasServerPath);
    }
}
