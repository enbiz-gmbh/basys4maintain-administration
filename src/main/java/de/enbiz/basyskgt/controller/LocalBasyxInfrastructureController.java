package de.enbiz.basyskgt.controller;

import de.enbiz.basyskgt.configuration.BasyxConfig;
import org.eclipse.basyx.components.aas.AASServerComponent;
import org.eclipse.basyx.components.aas.configuration.AASServerBackend;
import org.eclipse.basyx.components.aas.configuration.BaSyxAASServerConfiguration;
import org.eclipse.basyx.components.configuration.BaSyxContextConfiguration;
import org.eclipse.basyx.components.registry.RegistryComponent;
import org.eclipse.basyx.components.registry.configuration.BaSyxRegistryConfiguration;
import org.eclipse.basyx.components.registry.configuration.RegistryBackend;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;

/**
 * provides a local AAS server and registry
 */
@Controller
public class LocalBasyxInfrastructureController {

    final BasyxConfig basyxConfig;
    private final Logger log = LoggerFactory.getLogger(LocalBasyxInfrastructureController.class);
    private final LocalBasyxInfrastructureStatus status = new LocalBasyxInfrastructureStatus(false, false);
    private String registryPath;
    private String aasServerPath;
    private AASServerComponent aasServer;
    private RegistryComponent registry;

    @Autowired
    public LocalBasyxInfrastructureController(BasyxConfig basyxConfig) {
        this.basyxConfig = basyxConfig;
    }

    @PostConstruct
    void init() {
        log.info("Initializing local Basyx infrastructure");
        // assemble paths of AAS Server and Registry
        int registryPort = Integer.parseInt(basyxConfig.getLocalRegistryServerPort());
        String registryContextPath = basyxConfig.getLocalRegistryServerPath();
        int aasServerPort = Integer.parseInt(basyxConfig.getLocalAasServerPort());
        String aasServerContextPath = basyxConfig.getLocalAasServerPath();
        this.registryPath = "http://localhost:" + registryPort + registryContextPath;
        this.aasServerPath = "http://localhost:" + aasServerPort + aasServerContextPath;

        //create RegistryComponent
        BaSyxContextConfiguration registryContextConfig = new BaSyxContextConfiguration(registryPort, registryContextPath);
        BaSyxRegistryConfiguration registryConfig = new BaSyxRegistryConfiguration(RegistryBackend.INMEMORY);
        this.registry = new RegistryComponent(registryContextConfig, registryConfig);

        // create AASServerComponent
        BaSyxContextConfiguration aasServerContextConfig = new BaSyxContextConfiguration(aasServerPort, aasServerContextPath);
        BaSyxAASServerConfiguration aasServerConfig = new BaSyxAASServerConfiguration(AASServerBackend.INMEMORY, "", registryPath); //TODO change backend to DB
        this.aasServer = new AASServerComponent(aasServerContextConfig, aasServerConfig);

        log.info(String.format("Local Basyx infrastructure initialized with registryPath %s and aasServerPath %s", this.registryPath, this.aasServerPath));
    }

    public void startAasServer() {
        try {
            this.aasServer.startComponent();
            log.info(String.format("Local AAS server started at %s", this.aasServerPath));
            status.localAasServerRunning = true;
        } catch (Exception e) {
            log.error("Starting local AAS server failed");
            e.printStackTrace();
        }
    }

    public void startRegistry() {
        try {
            this.registry.startComponent();
            log.info(String.format("Local registry server started at %s", this.registryPath));
            status.localRegistryRunning = true;
        } catch (Exception e) {
            log.error("Starting local registry failed");
            e.printStackTrace();
        }
    }

    public void stopAasServer() {
        this.aasServer.stopComponent();
        log.info("Local AAS server stopped");
        status.localAasServerRunning = false;
    }

    public void stopRegistry() {
        this.registry.stopComponent();
        log.info("Local registry server stopped");
        status.localRegistryRunning = false;
    }

    public String getRegistryPath() {
        return registryPath;
    }

    public String getAasServerPath() {
        return aasServerPath;
    }

    public LocalBasyxInfrastructureStatus getStatus() {
        return status;
    }

    class LocalBasyxInfrastructureStatus {
        private boolean localRegistryRunning;
        private boolean localAasServerRunning;

        private LocalBasyxInfrastructureStatus(boolean localRegistryRunning, boolean localAasServerRunning) {
            this.localRegistryRunning = localRegistryRunning;
            this.localAasServerRunning = localAasServerRunning;
        }

        public boolean isLocalRegistryRunning() {
            return localRegistryRunning;
        }

        public boolean isLocalAasServerRunning() {
            return localAasServerRunning;
        }
    }
}
