package de.enbiz.basyskgt.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

/**
 * Class that holds the configuration for the BaSyx infrastructure.
 * Parameters are loaded from *.properties file by Spring.
 */
@ConfigurationProperties(prefix = "basyx.infrastructure")
@ConstructorBinding
public class BasyxInfrastructureConfig {

    private final String aasServerPath;
    private final String registryServerPath;
    private final int localRegistryServerPort;
    private final String localRegistryServerPath;
    private final int localAasServerPort;
    private final String localAasServerPath;
    private final boolean localAasServerEnabled;
    private final boolean localRegistryEnabled;

    /**
     * Default constructor.
     *
     * @param aasServerPath           URL of the AAS Server to be used. This will be ignored in favor of localAasServerPort and
     *                                localAasServerPath if localAasServerEnabled is set to "true"
     * @param registryServerPath      URL of the AAS Server to be used. This will be ignored in favor of localRegistryServerPort
     *                                and localRegistryServerPath if localRegistryEnabled is set to "true"
     * @param localRegistryServerPort port at which to start the local Registry Server if enabled
     * @param localRegistryServerPath resource path at which to start the local Registry Server if enabled
     * @param localAasServerPort      port at which to start the local Registry Server if enabled
     * @param localAasServerPath      resource path at which to start the local AAS Server if enabled
     * @param localAasServerEnabled   Flag to enable the integrated AAS Server. If enabled aasServerPath will be ignored.
     *                                Use localAasServerPort and localAasServerPath to configure location of integrated
     *                                AAS Server.
     * @param localRegistryEnabled    Flag to enable the integrated Registry Server. If enabled registryServerPath will be
     *                                ignored. Use localRegistryServerPort and localRegistryServerPath to configure location
     *                                of integrated Registry Server.
     */
    public BasyxInfrastructureConfig(String aasServerPath, String registryServerPath, int localRegistryServerPort,
                                     String localRegistryServerPath, int localAasServerPort, String localAasServerPath,
                                     boolean localAasServerEnabled, boolean localRegistryEnabled) {
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

    public int getLocalRegistryServerPort() {
        return localRegistryServerPort;
    }

    public String getLocalRegistryServerPath() {
        return localRegistryServerPath;
    }

    public int getLocalAasServerPort() {
        return localAasServerPort;
    }

    public String getLocalAasServerPath() {
        return localAasServerPath;
    }

    public boolean getLocalAasServerEnabled() {
        return localAasServerEnabled;
    }

    public boolean getLocalRegistryEnabled() {
        return localRegistryEnabled;
    }
}