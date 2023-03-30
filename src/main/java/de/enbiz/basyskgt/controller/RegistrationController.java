package de.enbiz.basyskgt.controller;

import de.enbiz.basyskgt.configuration.BasyxInfrastructureConfig;
import de.enbiz.basyskgt.configuration.PortConfiguration;
import de.enbiz.basyskgt.exceptions.AASXFileParseException;
import de.enbiz.basyskgt.storage.AasxFile;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.eclipse.basyx.aas.aggregator.proxy.AASAggregatorProxy;
import org.eclipse.basyx.aas.bundle.AASBundle;
import org.eclipse.basyx.aas.bundle.AASBundleHelper;
import org.eclipse.basyx.aas.registration.proxy.AASRegistryProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.Collection;
import java.util.Collections;

@Controller
public class RegistrationController {

    private static final Logger log = LoggerFactory.getLogger(RegistrationController.class);

    final AASRegistryProxy aasRegistryProxy;

    final AASAggregatorProxy aasAggregatorProxy;

    final BasyxInfrastructureConfig basyxInfrastructureConfig;

    final AasxImportController aasxImportController;
    private PortConfiguration portConfiguration;
    final RegistrationStatusController registrationStatusController;

    @Autowired
    public RegistrationController(AASRegistryProxy aasRegistryProxy, AASAggregatorProxy aasAggregatorProxy, BasyxInfrastructureConfig basyxInfrastructureConfig,
                                  AasxImportController aasxImportController, PortConfiguration portConfiguration, RegistrationStatusController registrationStatusController) {
        this.aasRegistryProxy = aasRegistryProxy;
        this.aasAggregatorProxy = aasAggregatorProxy;
        this.basyxInfrastructureConfig = basyxInfrastructureConfig;
        this.aasxImportController = aasxImportController;
        this.portConfiguration = portConfiguration;
        this.registrationStatusController = registrationStatusController;
    }

    /**
     * Register the shell for a port to the registry and upload it to the AAS server
     *
     * @param port the port that should be registered
     * @return true on successful upload and registration; false otherwise
     * @throws IllegalStateException    no AAS mapped to the given port or AAS already registered and uploaded
     * @throws IllegalArgumentException port does not exist
     * @throws AASXFileParseException   an unknown error occured while reading the AASX file
     * @throws InvalidFormatException   the AASX file contains too many AAS
     */
    public boolean register(int port) throws IllegalStateException, IllegalArgumentException, AASXFileParseException, InvalidFormatException {
        AasxFile aasxFile = getAasxFile(port);
        RegistrationStatusController.RegistrationStatus registrationStatus = registrationStatusController.refreshAndGetStatus(port);

        if (registrationStatus.registeredToAasRegistry() && registrationStatus.shellUploadedToRepository()) {
            throw new IllegalStateException("AAS is already registered and uploaded to server");
        }

        // get AASBundle for the port
        Collection<AASBundle> aasBundleSingleton = Collections.singleton(aasxImportController.getAasBundleFromAasx(aasxFile));

        // upload to AAS server
        if (!registrationStatus.shellUploadedToRepository()) {
            log.info("Uploading AAS and submodels to AAS server");
            boolean uploadSuccess = AASBundleHelper.integrate(aasAggregatorProxy, aasBundleSingleton);
            if (!uploadSuccess) {
                return false;
            }
        }

        // delete from registry
        if (!registrationStatus.registeredToAasRegistry()) {
            log.info("Registering AAS and submodels to registry");
            AASBundleHelper.register(aasRegistryProxy, aasBundleSingleton, basyxInfrastructureConfig.getAasServerPath());
        }

        registrationStatusController.refreshAndGetStatus(port);       // force update of registration status
        return true;
    }

    /**
     * deregister the shell for a port from the registry and delete it from the AAS server
     *
     * @param port the port that should be deregistered
     * @return true on successful deregistration and deletion; false otherwise
     * @throws IllegalStateException    no AAS mapped to the given port or AAS already registered and uploaded
     * @throws IllegalArgumentException port does not exist
     * @throws AASXFileParseException   an unknown error occured while reading the AASX file
     * @throws InvalidFormatException   the AASX file contains too many AAS
     */
    public boolean deregister(int port) throws IllegalStateException, IllegalArgumentException, AASXFileParseException, InvalidFormatException {
        AasxFile aasxFile = getAasxFile(port);
        RegistrationStatusController.RegistrationStatus registrationStatus = registrationStatusController.refreshAndGetStatus(port);

        if (!registrationStatus.registeredToAasRegistry() && !registrationStatus.shellUploadedToRepository()) {
            throw new IllegalStateException("AAS is currently not registered nor uploaded to the server");
        }

        // get AASBundle for the port
        Collection<AASBundle> aasBundleSingleton = Collections.singleton(aasxImportController.getAasBundleFromAasx(aasxFile));

        // delete from registry
        if (registrationStatus.registeredToAasRegistry()) {
            log.info("Deregistering AAS and submodels from registry");
            AASBundleHelper.deregister(aasRegistryProxy, aasBundleSingleton);
        }

        // delete from AAS server
        if (registrationStatus.shellUploadedToRepository()) {
            log.info("Deleting AAS and submodels from AAS server");
            aasAggregatorProxy.deleteAAS(aasBundleSingleton.iterator().next().getAAS().getIdentification());
        }

        registrationStatusController.refreshAndGetStatus(port);       // force update of registration status
        return true;
    }

    /**
     * retrieve the AASX file mapped to the given port
     *
     * @param port the port to check
     */
    private AasxFile getAasxFile(int port) {
        if (!portConfiguration.portExists(port)) {
            throw new IllegalArgumentException(String.format("Port number %d does not exist.", port));
        }
        AasxFile aasxFile = portConfiguration.getMappedFile(port);
        if (aasxFile == null) {
            throw new IllegalStateException(String.format("Port %d is not mapped to an AAS", port));
        }
        return aasxFile;
    }

}
