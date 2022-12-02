package de.enbiz.basyskgt;

import de.enbiz.basyskgt.configuration.BasyxInfrastructureConfig;
import de.enbiz.basyskgt.controller.LocalBasyxInfrastructureController;
import de.enbiz.basyskgt.controller.RegistrationController;
import org.eclipse.basyx.aas.manager.ConnectedAssetAdministrationShellManager;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.connected.ConnectedAssetAdministrationShell;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@EnableConfigurationProperties(BasyxInfrastructureConfig.class)
@PropertySource("classpath:basyx.properties")
public class BasysKgtApplication implements CommandLineRunner {

    final BasyxInfrastructureConfig basyxInfrastructureConfig;
    final LocalBasyxInfrastructureController localBasyxInfrastructureController;
    final ConnectedAssetAdministrationShellManager aasManager;
    final IAssetAdministrationShell bsAas;
    final RegistrationController registrationController;
    private final Logger log = LoggerFactory.getLogger(BasysKgtApplication.class);

    @Autowired
    public BasysKgtApplication(BasyxInfrastructureConfig basyxInfrastructureConfig, LocalBasyxInfrastructureController localBasyxInfrastructureController, ConnectedAssetAdministrationShellManager aasManager, IAssetAdministrationShell bsAas, RegistrationController registrationController) {
        this.basyxInfrastructureConfig = basyxInfrastructureConfig;
        this.localBasyxInfrastructureController = localBasyxInfrastructureController;
        this.aasManager = aasManager;
        this.bsAas = bsAas;
        this.registrationController = registrationController;
    }

    public static void main(String[] args) {
        SpringApplication.run(BasysKgtApplication.class, args);
    }

    @Override
    public void run(String... args) {
        log.info("KGT Application starting up...");

        if (basyxInfrastructureConfig.getLocalAasServerEnabled()) {
            log.info("Local AAS server enabled. Starting it now.");
            localBasyxInfrastructureController.startAasServer();
        } else {
            log.info("Local AAS Server disabled.");
        }
        if (basyxInfrastructureConfig.getLocalRegistryEnabled()) {
            log.info("Local registry enabled. Starting it now.");
            localBasyxInfrastructureController.startRegistry();
        } else {
            log.info("Local registry disabled.");
        }

        log.info("Checking if AAS is already registered...");
        ConnectedAssetAdministrationShell connectedBsAas = null;
        try {
            connectedBsAas = aasManager.retrieveAAS(bsAas.getIdentification());
        } catch (ResourceNotFoundException e) {
            log.info("Query to AAS server / registry failed. Either the server is offline or the AAS is not registered.");
            e.printStackTrace();
        }
        if (connectedBsAas != null) {
            log.info(String.format("AAS is already registered at server %s", basyxInfrastructureConfig.getAasServerPath()));
            registrationController.setRegisteredToAasRegistry(true);
            registrationController.setShellUploadedToRepository(true);
        } else {
            log.error("AAS registration failed. Please make sure the server is online and reachable.");
            registrationController.setRegisteredToAasRegistry(false);
            registrationController.setShellUploadedToRepository(false);
        }


        log.info("KGT Application startup complete");
    }
}
