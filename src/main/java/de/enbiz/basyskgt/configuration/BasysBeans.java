package de.enbiz.basyskgt.configuration;

import org.eclipse.basyx.aas.aggregator.proxy.AASAggregatorProxy;
import org.eclipse.basyx.aas.manager.ConnectedAssetAdministrationShellManager;
import org.eclipse.basyx.aas.registration.proxy.AASRegistryProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Provides various Beans for working with the BaSyx infrastructure
 */
@Configuration
public class BasysBeans {

    final BasyxConfig basyxConfig;

    @Autowired
    public BasysBeans(BasyxConfig basyxConfig) {
        this.basyxConfig = basyxConfig;
    }

    @Bean
    AASRegistryProxy aasRegistryProxy() {
        return new AASRegistryProxy(basyxConfig.getRegistryServerPath());
    }

    @Bean
    ConnectedAssetAdministrationShellManager connectedAssetAdministrationShellManager(AASRegistryProxy aasRegistryProxy) {
        return new ConnectedAssetAdministrationShellManager(aasRegistryProxy);
    }

    @Bean
    AASAggregatorProxy aasAggregatorProxy() {
        return new AASAggregatorProxy(basyxConfig.getAasServerPath());
    }
}
