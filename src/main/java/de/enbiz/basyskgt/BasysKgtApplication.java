package de.enbiz.basyskgt;

import de.enbiz.basyskgt.basyxInfrastructureConnection.BasyxInfrastructureStatusController;
import de.enbiz.basyskgt.configuration.BasyxInfrastructureConfig;
import de.enbiz.basyskgt.controller.RegistrationController;
import org.eclipse.basyx.aas.manager.ConnectedAssetAdministrationShellManager;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableConfigurationProperties(BasyxInfrastructureConfig.class)
@PropertySource("classpath:basyx.properties")
@EnableScheduling
public class BasysKgtApplication implements CommandLineRunner {

    final BasyxInfrastructureConfig basyxInfrastructureConfig;
    final ConnectedAssetAdministrationShellManager aasManager;
    final IAssetAdministrationShell bsAas;
    final RegistrationController registrationController;
    final BasyxInfrastructureStatusController infrastructureStatus;
    private final Logger log = LoggerFactory.getLogger(BasysKgtApplication.class);

    @Autowired
    public BasysKgtApplication(BasyxInfrastructureConfig basyxInfrastructureConfig, ConnectedAssetAdministrationShellManager aasManager, IAssetAdministrationShell bsAas, RegistrationController registrationController, BasyxInfrastructureStatusController infrastructureStatus) {
        this.basyxInfrastructureConfig = basyxInfrastructureConfig;
        this.aasManager = aasManager;
        this.bsAas = bsAas;
        this.registrationController = registrationController;
        this.infrastructureStatus = infrastructureStatus;
    }

    public static void main(String[] args) {
        SpringApplication.run(BasysKgtApplication.class, args);
    }

    @Override
    public void run(String... args) {
        log.info("KGT Application starting up...");

        waitForInfrastructureAccess();

        RegistrationController.RegistrationStatusDAO registrationStatus = registrationController.getStatus();
        log.info("AAS registered to registry: {}", registrationStatus.registeredToAasRegistry());
        log.info("AAS uploaded to repository: {}", registrationStatus.shellUploadedToRepository());

        log.info("KGT Application startup complete");
    }

    private void waitForInfrastructureAccess() {
        boolean aasServerAccess = false;
        boolean registryAccess = false;

        while (!(aasServerAccess && registryAccess)) {
            log.info("Trying to connect to AAS server and registry ...");
            aasServerAccess = infrastructureStatus.checkAasServerAccess();
            registryAccess = infrastructureStatus.checkRegistryAccess();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        log.info("AAS server and registry connection established.");
    }
}
