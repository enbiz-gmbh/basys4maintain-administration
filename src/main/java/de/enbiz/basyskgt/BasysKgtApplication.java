package de.enbiz.basyskgt;

import de.enbiz.basyskgt.basyx.LocalBasyxInfrastructureService;
import de.enbiz.basyskgt.model.RegistrationStatus;
import de.enbiz.basyskgt.persistence.ConfigParameter;
import de.enbiz.basyskgt.persistence.ConfigRepository;
import org.eclipse.basyx.aas.manager.ConnectedAssetAdministrationShellManager;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.connected.ConnectedAssetAdministrationShell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BasysKgtApplication implements CommandLineRunner {

	private final Logger log = LoggerFactory.getLogger(BasysKgtApplication.class);

	@Autowired
	ConfigRepository configRepository;

	@Autowired
	LocalBasyxInfrastructureService localBasyxInfrastructureService;

	@Autowired
	ConnectedAssetAdministrationShellManager aasManager;

	@Autowired
	IAssetAdministrationShell bsAas;

	@Autowired
	RegistrationStatus registrationStatus;

	public static void main(String[] args) {
		SpringApplication.run(BasysKgtApplication.class, args);
	}

	@Override
	public void run(String... args) {
		log.info("KGT Application starting up...");

		if ("true".equals(configRepository.getConfigParameter(ConfigParameter.LOCAL_REGISTRY_AND_AAS_SERVER_ENABLED))) {
			log.info("Local AAS server and registry enabled");
			localBasyxInfrastructureService.start();
		}

		log.info("Checking if AAS is already registered...");
		try {
			ConnectedAssetAdministrationShell connectedBsAas = aasManager.retrieveAAS(bsAas.getIdentification());
			if (connectedBsAas != null) {
				log.info(String.format("AAS is already registered at server %s", configRepository.getConfigParameter(ConfigParameter.AAS_SERVER_PATH)));
				registrationStatus.setRegisteredToAasRegistry(true);
				registrationStatus.setShellUploadedToRepository(true);
			} else {
				log.info("AAS is not registered");
				registrationStatus.setRegisteredToAasRegistry(false);
				registrationStatus.setShellUploadedToRepository(false);
			}
		} catch (Exception e) {
			log.info("Query to AAS server / registry failed: " + e.getMessage());
		}

		log.info("KGT Application startup complete");
	}
}
