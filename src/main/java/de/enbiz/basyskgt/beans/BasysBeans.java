package de.enbiz.basyskgt.beans;

import de.enbiz.basyskgt.persistence.ConfigParameter;
import de.enbiz.basyskgt.persistence.ConfigRepository;
import org.eclipse.basyx.aas.manager.ConnectedAssetAdministrationShellManager;
import org.eclipse.basyx.aas.registration.proxy.AASRegistryProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BasysBeans {

    @Autowired
    ConfigRepository configRepository;

    @Bean
    AASRegistryProxy aasRegistryProxy() {
        return new AASRegistryProxy(configRepository.getConfigParameter(ConfigParameter.REGISTRY_SERVER_PATH).getValue());
    }

    @Bean
    ConnectedAssetAdministrationShellManager connectedAssetAdministrationShellManager(AASRegistryProxy aasRegistryProxy) {
        return new ConnectedAssetAdministrationShellManager(aasRegistryProxy);
    }
}
