package de.enbiz.basyskgt.controller;

import de.enbiz.basyskgt.configuration.BasyxInfrastructureConfig;
import org.eclipse.basyx.aas.aggregator.proxy.AASAggregatorProxy;
import org.eclipse.basyx.aas.bundle.AASBundle;
import org.eclipse.basyx.aas.bundle.AASBundleHelper;
import org.eclipse.basyx.aas.registration.proxy.AASRegistryProxy;
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

    @Autowired
    public RegistrationController(AASRegistryProxy aasRegistryProxy, AASAggregatorProxy aasAggregatorProxy,
                                  BasyxInfrastructureConfig basyxInfrastructureConfig, AASBundle bsAasBundle) {
        this.aasRegistryProxy = aasRegistryProxy;
        this.aasAggregatorProxy = aasAggregatorProxy;
        this.basyxInfrastructureConfig = basyxInfrastructureConfig;
        this.bsAasBundle = bsAasBundle;
    }

    public boolean register() throws IllegalStateException {
        if (registeredToAasRegistry && shellUploadedToRepository) {
            throw new IllegalStateException("AAS is already registered and uploaded to server");
        }
        if (!shellUploadedToRepository) {
            log.info("Uploading AAS and submodels to AAS server");
            AASBundleHelper.integrate(aasAggregatorProxy, Collections.singleton(bsAasBundle));
            setShellUploadedToRepository(true);
        }
        if (!registeredToAasRegistry) {
            log.info("Registering AAS and submodels to registry");
            AASBundleHelper.register(aasRegistryProxy, Collections.singleton(bsAasBundle), basyxInfrastructureConfig.getAasServerPath());
            setRegisteredToAasRegistry(true);
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
            setRegisteredToAasRegistry(false);
        }
        if (shellUploadedToRepository) {
            log.info("Deleting AAS and submodels from AAS server");
            aasAggregatorProxy.deleteAAS(bsAasBundle.getAAS().getIdentification());
            setShellUploadedToRepository(false);
        }
        return true;
    }

    public boolean isRegisteredToAasRegistry() {
        return registeredToAasRegistry;
    }

    public void setRegisteredToAasRegistry(boolean registeredToAasRegistry) {
        this.registeredToAasRegistry = registeredToAasRegistry;
    }

    public boolean isShellUploadedToRepository() {
        return shellUploadedToRepository;
    }

    public void setShellUploadedToRepository(boolean shellUploadedToRepository) {
        this.shellUploadedToRepository = shellUploadedToRepository;
    }

    public RegistrationStatusDAO getStatus() {
        return new RegistrationStatusDAO(isRegisteredToAasRegistry(), isShellUploadedToRepository());
    }

    public record RegistrationStatusDAO(boolean registeredToAasRegistry, boolean shellUploadedToRepository) {

    }
}
