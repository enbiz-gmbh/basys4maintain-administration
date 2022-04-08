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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LocalBasyxInfrastructureComponent {

	private final String registryPath;
	private final String aasServerPath;

	private final AASServerComponent aasServer;
	private final RegistryComponent registry;

	@Autowired
	ConfigRepository configRepository;

	private LocalBasyxInfrastructureComponent() {
		// assemble paths of AAS Server and Registry
		int registryPort = Integer.parseInt(configRepository.getConfigParameter(ConfigParameter.LOCAL_REGISTRY_SERVER_PORT).getValue());
		String registryContextPath = configRepository.getConfigParameter(ConfigParameter.LOCAL_REGISTRY_SERVER_PATH).getValue();
		int aasServerPort = Integer.parseInt(configRepository.getConfigParameter(ConfigParameter.LOCAL_AAS_SERVER_PORT).getValue());
		String aasServerContextPath = configRepository.getConfigParameter(ConfigParameter.LOCAL_AAS_SERVER_PATH).getValue();
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
	}

	public void start() {
		this.aasServer.startComponent();
		this.registry.startComponent();
	}

	public void stop() {
		this.aasServer.stopComponent();
		this.registry.stopComponent();
	}

	public String getRegistryPath() {
		return registryPath;
	}

	public String getAasServerPath() {
		return aasServerPath;
	}
}
