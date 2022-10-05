package de.enbiz.basyskgt.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties(prefix = "basyx.infrastructure")
@ConstructorBinding
public class BasyxConfig {

    private final String aasServerPath;
    private final String registryServerPath;
    private final String localRegistryServerPort;
    private final String localRegistryServerPath;
    private final String localAasServerPort;
    private final String localAasServerPath;
    private final String localAasServerEnabled;
    private final String localRegistryEnabled;

    public BasyxConfig(String aasServerPath, String registryServerPath, String localRegistryServerPort, String localRegistryServerPath, String localAasServerPort, String localAasServerPath, String localAasServerEnabled, String localRegistryEnabled) {
        this.aasServerPath = aasServerPath;
        this.registryServerPath = registryServerPath;
        this.localRegistryServerPort = localRegistryServerPort;
        this.localRegistryServerPath = localRegistryServerPath;
        this.localAasServerPort = localAasServerPort;
        this.localAasServerPath = localAasServerPath;
        this.localAasServerEnabled = localAasServerEnabled;
        this.localRegistryEnabled = localRegistryEnabled;
    }

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

    public String getLocalAasServerEnabled() {
        return localAasServerEnabled;
    }

    public String getLocalRegistryEnabled() {
        return localRegistryEnabled;
    }
}