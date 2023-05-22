package de.enbiz.basyskgt;

import de.enbiz.basyskgt.basyxInfrastructureConnection.BasyxInfrastructureStatusController;
import de.enbiz.basyskgt.configuration.BasyxInfrastructureConfig;
import de.enbiz.basyskgt.controller.RegistrationStatusController;
import org.eclipse.basyx.aas.manager.ConnectedAssetAdministrationShellManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Arrays;

@SpringBootApplication
@EnableConfigurationProperties(BasyxInfrastructureConfig.class)
@PropertySource("classpath:basyx.properties")
@EnableScheduling
public class BasysKgtApplication implements CommandLineRunner {

    final BasyxInfrastructureConfig basyxInfrastructureConfig;
    final ConnectedAssetAdministrationShellManager aasManager;
    final RegistrationStatusController registrationStatusController;
    final BasyxInfrastructureStatusController infrastructureStatus;
    private final Logger log = LoggerFactory.getLogger(BasysKgtApplication.class);

    @Autowired
    public BasysKgtApplication(BasyxInfrastructureConfig basyxInfrastructureConfig, ConnectedAssetAdministrationShellManager aasManager, RegistrationStatusController registrationStatusController, BasyxInfrastructureStatusController infrastructureStatus) {
        this.basyxInfrastructureConfig = basyxInfrastructureConfig;
        this.aasManager = aasManager;
        this.registrationStatusController = registrationStatusController;
        this.infrastructureStatus = infrastructureStatus;
    }

    public static void main(String[] args) {
        SpringApplication.run(BasysKgtApplication.class, args);
    }

    @Override
    public void run(String... args) {
        log.info("KGT Application starting up...");

        waitForInfrastructureAccess();

        RegistrationStatusController.RegistrationStatus[] registrationStatus = registrationStatusController.refreshAndGetAllRegistrationStatus();
        log.info("Registration status: {}", Arrays.toString(registrationStatus));

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
