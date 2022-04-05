package de.enbiz.basyskgt.model;

import lombok.Getter;

public class ServerConfig {

    public ServerConfig(String registryPath, String aasServerPath) {
        this.registryPath = registryPath;
        this.aasServerPath = aasServerPath;
    }

    @Getter
    private String registryPath;

    @Getter
    private String aasServerPath;
}
