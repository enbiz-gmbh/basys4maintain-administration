package de.enbiz.basyskgt;

import de.enbiz.basyskgt.basyx.LocalBasyxInfrastructureService;
import de.enbiz.basyskgt.model.RegistrationStatus;
import de.enbiz.basyskgt.persistence.ConfigParameter;
import de.enbiz.basyskgt.persistence.ConfigRepository;
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
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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

	RegistrationStatus registrationStatus = RegistrationStatus.getInstance();

	public static void main(String[] args) {
		SpringApplication.run(BasysKgtApplication.class, args);
	}

	@Override
	public void run(String... args) {
		log.info("KGT Application starting up...");

		if ("true".equals(configRepository.getConfigEntry(ConfigParameter.LOCAL_REGISTRY_AND_AAS_SERVER_ENABLED).getValue())) {
			log.info("Local AAS server and registry enabled");
			localBasyxInfrastructureService.start();
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
			log.info(String.format("AAS is already registered at server %s", configRepository.getConfigEntry(ConfigParameter.AAS_SERVER_PATH)));
			registrationStatus.setRegisteredToAasRegistry(true);
			registrationStatus.setShellUploadedToRepository(true);
		} else {
			log.info("AAS is not registered. Please make sure the server is online.");
			registrationStatus.setRegisteredToAasRegistry(false);
			registrationStatus.setShellUploadedToRepository(false);
		}


		log.info("KGT Application startup complete");
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/api/**").allowedOrigins("*");
			}
		};
	}
}
