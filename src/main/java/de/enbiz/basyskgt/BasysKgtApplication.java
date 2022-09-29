package de.enbiz.basyskgt;

import de.enbiz.basyskgt.basyx.LocalBasyxInfrastructureService;
import de.enbiz.basyskgt.model.RegistrationStatus;
import de.enbiz.basyskgt.persistence.BasyxConfig;
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
@EnableConfigurationProperties(BasyxConfig.class)
@PropertySource("classpath:basyx.properties")
public class BasysKgtApplication implements CommandLineRunner {

    final BasyxConfig basyxConfig;
    final LocalBasyxInfrastructureService localBasyxInfrastructureService;
    final ConnectedAssetAdministrationShellManager aasManager;
    final IAssetAdministrationShell bsAas;
    private final Logger log = LoggerFactory.getLogger(BasysKgtApplication.class);
    RegistrationStatus registrationStatus = RegistrationStatus.getInstance();

    @Autowired
    public BasysKgtApplication(BasyxConfig basyxConfig, LocalBasyxInfrastructureService localBasyxInfrastructureService, ConnectedAssetAdministrationShellManager aasManager, IAssetAdministrationShell bsAas) {
        this.basyxConfig = basyxConfig;
        this.localBasyxInfrastructureService = localBasyxInfrastructureService;
        this.aasManager = aasManager;
        this.bsAas = bsAas;
    }

    public static void main(String[] args) {
        SpringApplication.run(BasysKgtApplication.class, args);
    }

    @Override
    public void run(String... args) {
        log.info("KGT Application starting up...");

        if ("true".equals(basyxConfig.getLocalAasServerEnabled())) {
            log.info("Local AAS server enabled");
            localBasyxInfrastructureService.startAasServer();
        }
        if ("true".equals(basyxConfig.getLocalRegistryEnabled())) {
            log.info("Local registry enabled");
            localBasyxInfrastructureService.startRegistry();
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
            log.info(String.format("AAS is already registered at server %s", basyxConfig.getAasServerPath()));
            registrationStatus.setRegisteredToAasRegistry(true);
            registrationStatus.setShellUploadedToRepository(true);
        } else {
            log.info("AAS is not registered. Please make sure the server is online.");
            registrationStatus.setRegisteredToAasRegistry(false);
            registrationStatus.setShellUploadedToRepository(false);
        }


        log.info("KGT Application startup complete");
    }
}
