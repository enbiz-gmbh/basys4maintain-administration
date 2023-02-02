package de.enbiz.basyskgt.controller;

import de.enbiz.basyskgt.configuration.BasyxInfrastructureConfig;
import org.eclipse.basyx.aas.aggregator.proxy.AASAggregatorProxy;
import org.eclipse.basyx.aas.bundle.AASBundle;
import org.eclipse.basyx.aas.bundle.AASBundleHelper;
import org.eclipse.basyx.aas.manager.ConnectedAssetAdministrationShellManager;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.connected.ConnectedAssetAdministrationShell;
import org.eclipse.basyx.aas.registration.proxy.AASRegistryProxy;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.Collections;

@Controller
public class RegistrationController {

    private static final Logger log = LoggerFactory.getLogger(RegistrationController.class);

    final AASRegistryProxy aasRegistryProxy;

    final AASAggregatorProxy aasAggregatorProxy;

    final BasyxInfrastructureConfig basyxInfrastructureConfig;

    final AASBundle bsAasBundle;

    private boolean registeredToAasRegistry;

    private boolean shellUploadedToRepository;
    private ConnectedAssetAdministrationShellManager aasManager;
    private IAssetAdministrationShell bsAas;

    @Autowired
    public RegistrationController(AASRegistryProxy aasRegistryProxy, AASAggregatorProxy aasAggregatorProxy,
                                  BasyxInfrastructureConfig basyxInfrastructureConfig, AASBundle bsAasBundle, ConnectedAssetAdministrationShellManager aasManager, IAssetAdministrationShell bsAas) {
        this.aasRegistryProxy = aasRegistryProxy;
        this.aasAggregatorProxy = aasAggregatorProxy;
        this.basyxInfrastructureConfig = basyxInfrastructureConfig;
        this.bsAasBundle = bsAasBundle;
        this.aasManager = aasManager;
        this.bsAas = bsAas;
    }

    public boolean register() throws IllegalStateException {
        if (registeredToAasRegistry && shellUploadedToRepository) {
            throw new IllegalStateException("AAS is already registered and uploaded to server");
        }
        if (!shellUploadedToRepository) {
            log.info("Uploading AAS and submodels to AAS server");
            AASBundleHelper.integrate(aasAggregatorProxy, Collections.singleton(bsAasBundle));
            shellUploadedToRepository = true;
        }
        if (!registeredToAasRegistry) {
            log.info("Registering AAS and submodels to registry");
            AASBundleHelper.register(aasRegistryProxy, Collections.singleton(bsAasBundle), basyxInfrastructureConfig.getAasServerPath());
            registeredToAasRegistry = true;
        }
        return true;
    }

    public boolean deregister() throws IllegalStateException {
        if (!registeredToAasRegistry && !shellUploadedToRepository) {
            throw new IllegalStateException("AAS is currently not registered");
        }
        if (registeredToAasRegistry) {
            log.info("Deregistering AAS and submodels from registry");
            AASBundleHelper.deregister(aasRegistryProxy, Collections.singleton(bsAasBundle));
            registeredToAasRegistry = false;
        }
        if (shellUploadedToRepository) {
            log.info("Deleting AAS and submodels from AAS server");
            aasAggregatorProxy.deleteAAS(bsAasBundle.getAAS().getIdentification());
            shellUploadedToRepository = false;
        }
        return true;
    }

    private void refreshRegistrationStatus() {
        log.debug("checking AAS registration status...");
        ConnectedAssetAdministrationShell connectedBsAas = null;
        try {
            connectedBsAas = aasManager.retrieveAAS(bsAas.getIdentification());
        } catch (ResourceNotFoundException e) {
            log.info("Query to AAS server / registry failed. Either the server is offline or the AAS is not registered. See stacktrace for more info.");
            e.printStackTrace();
        }
        if (connectedBsAas != null) {
            log.info(String.format("AAS is already registered at server %s", basyxInfrastructureConfig.getAasServerPath()));
            registeredToAasRegistry = true;
            shellUploadedToRepository = true;
        } else {
            log.info("AAS not currently registered");
            registeredToAasRegistry = false;
            shellUploadedToRepository = false;
        }
    }

    public RegistrationStatusDAO getStatus() {
        refreshRegistrationStatus();
        return new RegistrationStatusDAO(registeredToAasRegistry, shellUploadedToRepository);
    }

    public record RegistrationStatusDAO(boolean registeredToAasRegistry, boolean shellUploadedToRepository) {

    }
}
