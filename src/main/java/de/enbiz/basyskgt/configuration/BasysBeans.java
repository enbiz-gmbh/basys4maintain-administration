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

    final BasyxInfrastructureConfig basyxInfrastructureConfig;

    @Autowired
    public BasysBeans(BasyxInfrastructureConfig basyxInfrastructureConfig) {
        this.basyxInfrastructureConfig = basyxInfrastructureConfig;
    }

    @Bean
    AASRegistryProxy aasRegistryProxy() {
        return new AASRegistryProxy(basyxInfrastructureConfig.getRegistryServerPath());
    }

    @Bean
    ConnectedAssetAdministrationShellManager connectedAssetAdministrationShellManager(AASRegistryProxy aasRegistryProxy) {
        return new ConnectedAssetAdministrationShellManager(aasRegistryProxy);
    }

    @Bean
    AASAggregatorProxy aasAggregatorProxy() {
        return new AASAggregatorProxy(basyxInfrastructureConfig.getAasServerPath());
    }
}
