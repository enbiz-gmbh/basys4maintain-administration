package de.enbiz.basyskgt.controller;

import de.enbiz.basyskgt.configuration.BasyxInfrastructureConfig;
import de.enbiz.basyskgt.configuration.PortConfiguration;
import de.enbiz.basyskgt.exceptions.AASXFileParseException;
import de.enbiz.basyskgt.storage.AasxFile;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.eclipse.basyx.aas.aggregator.proxy.AASAggregatorProxy;
import org.eclipse.basyx.aas.bundle.AASBundle;
import org.eclipse.basyx.aas.bundle.AASBundleHelper;
import org.eclipse.basyx.aas.manager.ConnectedAssetAdministrationShellManager;
import org.eclipse.basyx.aas.metamodel.connected.ConnectedAssetAdministrationShell;
import org.eclipse.basyx.aas.registration.proxy.AASRegistryProxy;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
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

    private boolean[] registeredToAasRegistry;      // registration status of shells; registeredToAasRegistry[i]==true iff shell mapped to port i is registered

    private boolean[] shellUploadedToRepository;    // upload status of shells; shellUploadedToRepository[i]==true iff shell mapped to port i is uploaded
    private ConnectedAssetAdministrationShellManager aasManager;
    private PortConfiguration portConfiguration;

    @Autowired
    public RegistrationController(AASRegistryProxy aasRegistryProxy, AASAggregatorProxy aasAggregatorProxy, BasyxInfrastructureConfig basyxInfrastructureConfig,
                                  AasxImportController aasxImportController, ConnectedAssetAdministrationShellManager aasManager, PortConfiguration portConfiguration) {
        this.aasRegistryProxy = aasRegistryProxy;
        this.aasAggregatorProxy = aasAggregatorProxy;
        this.basyxInfrastructureConfig = basyxInfrastructureConfig;
        this.aasxImportController = aasxImportController;
        this.aasManager = aasManager;
        this.portConfiguration = portConfiguration;
        this.registeredToAasRegistry = new boolean[portConfiguration.NUM_PORTS];
        this.shellUploadedToRepository = new boolean[portConfiguration.NUM_PORTS];
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
        // check preconditions
        AasxFile aasxFile = getAasxFileAndPrepareRegistrationAction(port);

        if (registeredToAasRegistry[port] && shellUploadedToRepository[port]) {
            throw new IllegalStateException("AAS is already registered and uploaded to server");
        }

        // get AASBundle for the port
        Collection<AASBundle> aasBundleSingleton = Collections.singleton(aasxImportController.getAasBundleFromAasx(aasxFile));

        // upload to AAS server
        if (!shellUploadedToRepository[port]) {
            log.info("Uploading AAS and submodels to AAS server");
            boolean uploadSuccess = AASBundleHelper.integrate(aasAggregatorProxy, aasBundleSingleton);
            if (!uploadSuccess) {
                return false;
            }
            shellUploadedToRepository[port] = true;
        }

        // delete from registry
        if (!registeredToAasRegistry[port]) {
            log.info("Registering AAS and submodels to registry");
            AASBundleHelper.register(aasRegistryProxy, aasBundleSingleton, basyxInfrastructureConfig.getAasServerPath());
            registeredToAasRegistry[port] = true;
        }

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
        // check preconditions
        AasxFile aasxFile = getAasxFileAndPrepareRegistrationAction(port);

        if (!registeredToAasRegistry[port] && !shellUploadedToRepository[port]) {
            throw new IllegalStateException("AAS is currently not registered nor uploaded to the server");
        }

        // get AASBundle for the port
        Collection<AASBundle> aasBundleSingleton = Collections.singleton(aasxImportController.getAasBundleFromAasx(aasxFile));

        // delete from registry
        if (registeredToAasRegistry[port]) {
            log.info("Deregistering AAS and submodels from registry");
            AASBundleHelper.deregister(aasRegistryProxy, aasBundleSingleton);
            registeredToAasRegistry[port] = false;
        }

        // delete from AAS server
        if (shellUploadedToRepository[port]) {
            log.info("Deleting AAS and submodels from AAS server");
            aasAggregatorProxy.deleteAAS(aasBundleSingleton.iterator().next().getAAS().getIdentification());
            shellUploadedToRepository[port] = false;
        }

        return true;
    }

    /**
     * retrieve the AASX file mapped to the given port and prepare the port for a registration action.
     * Must be called before registering or deregistering a port
     *
     * @param port the port to check
     */
    private AasxFile getAasxFileAndPrepareRegistrationAction(int port) {
        if (!portConfiguration.portExists(port)) {
            throw new IllegalArgumentException(String.format("Port number %d does not exist.", port));
        }
        AasxFile aasxFile = portConfiguration.getMappedFile(port);
        if (aasxFile == null) {
            throw new IllegalStateException(String.format("Port %d is not mapped to an AAS", port));
        }
        refreshRegistrationStatus(port);
        return aasxFile;
    }

    private void refreshRegistrationStatus() {
        for (int port = 0; port < portConfiguration.NUM_PORTS; port++) {
            refreshRegistrationStatus(port);
        }
    }

    private void refreshRegistrationStatus(int port) {
        log.debug("checking AAS registration status for port {}...", port);
        ConnectedAssetAdministrationShell connectedBsAas = null;
        IIdentifier identifier = portConfiguration.getMappedAasIdentifier(port);

        if (identifier != null) {
            try {
                connectedBsAas = aasManager.retrieveAAS(identifier);
            } catch (ResourceNotFoundException e) {
                log.debug("Query to AAS server / registry failed. AAS is not registered: {}", e.getMessage());
            }
        }
        if (connectedBsAas != null) {
            log.info(String.format("AAS is already registered at server %s", basyxInfrastructureConfig.getAasServerPath()));
            registeredToAasRegistry[port] = true;
            shellUploadedToRepository[port] = true;
        } else {
            log.info("AAS not currently registered");
            registeredToAasRegistry[port] = false;
            shellUploadedToRepository[port] = false;
        }
    }

    public RegistrationStatusDAO getStatus(int port) throws IllegalArgumentException {
        if (!portConfiguration.portExists(port)) {
            throw new IllegalArgumentException(String.format("Port number %d does not exist.", port));
        }
        refreshRegistrationStatus(port);
        return new RegistrationStatusDAO(port, registeredToAasRegistry[port], shellUploadedToRepository[port]);
    }

    public RegistrationStatusDAO[] getAllRegistrationStatus() {
        refreshRegistrationStatus();
        RegistrationStatusDAO[] result = new RegistrationStatusDAO[portConfiguration.NUM_PORTS];
        for (int i = 0; i < result.length; i++) {
            result[i] = new RegistrationStatusDAO(i, registeredToAasRegistry[i], shellUploadedToRepository[i]);
        }
        return result;
    }

    public record RegistrationStatusDAO(int portNumber, boolean registeredToAasRegistry, boolean shellUploadedToRepository) {

    }
}
