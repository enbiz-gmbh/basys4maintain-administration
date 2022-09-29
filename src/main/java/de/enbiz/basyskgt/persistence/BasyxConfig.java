package de.enbiz.basyskgt.persistence;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "basyx.infrastructure")
public class BasyxConfig {

    private String aasServerPath;
    private String registryServerPath;
    private String localRegistryServerPort;
    private String localRegistryServerPath;
    private String localAasServerPort;
    private String localAasServerPath;
    private String localRegistryAndAasServerEnabled;

    public String getAasServerPath() {
        return aasServerPath;
    }

    public String getRegistryServerPath() {
        return registryServerPath;
    }

    public String getLocalRegistryServerPort() {
        return localRegistryServerPort;
    }

    public String getLocalRegistryServerPath() {
        return localRegistryServerPath;
    }

    public String getLocalAasServerPort() {
        return localAasServerPort;
    }

    public String getLocalAasServerPath() {
        return localAasServerPath;
    }

    public String getLocalRegistryAndAasServerEnabled() {
        return localRegistryAndAasServerEnabled;
    }
}