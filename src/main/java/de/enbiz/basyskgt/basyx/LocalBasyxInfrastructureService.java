package de.enbiz.basyskgt.basyx;

import de.enbiz.basyskgt.persistence.ConfigParameter;
import de.enbiz.basyskgt.persistence.ConfigRepository;
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
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * provides a local AAS server and registry
 */
@Service
public class LocalBasyxInfrastructureService {

	private Logger log = LoggerFactory.getLogger(LocalBasyxInfrastructureService.class);

	private String registryPath;
	private String aasServerPath;

	private AASServerComponent aasServer;
	private RegistryComponent registry;

	@Autowired
	ConfigRepository configRepository;

	@PostConstruct
	void init() {
		log.info("Initializing local Basyx infrastructure");
		// assemble paths of AAS Server and Registry
		int registryPort = Integer.parseInt(configRepository.getConfigEntry(ConfigParameter.LOCAL_REGISTRY_SERVER_PORT).getValue());
		String registryContextPath = configRepository.getConfigEntry(ConfigParameter.LOCAL_REGISTRY_SERVER_PATH).getValue();
		int aasServerPort = Integer.parseInt(configRepository.getConfigEntry(ConfigParameter.LOCAL_AAS_SERVER_PORT).getValue());
		String aasServerContextPath = configRepository.getConfigEntry(ConfigParameter.LOCAL_AAS_SERVER_PATH).getValue();
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

	public void start() {
		this.aasServer.startComponent();
		log.info(String.format("Local AAS server started at %s", this.aasServerPath));
		this.registry.startComponent();
		log.info(String.format("Local registry server started at %s", this.registryPath));
	}

	public void stop() {
		this.aasServer.stopComponent();
		log.info("Local AAS server stopped");
		this.registry.stopComponent();
		log.info("Local registry server stopped");
	}

	public String getRegistryPath() {
		return registryPath;
	}

	public String getAasServerPath() {
		return aasServerPath;
	}
}
