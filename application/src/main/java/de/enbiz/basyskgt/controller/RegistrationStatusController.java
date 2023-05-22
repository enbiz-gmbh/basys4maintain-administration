package de.enbiz.basyskgt.controller;

import de.enbiz.basyskgt.configuration.BasyxInfrastructureConfig;
import de.enbiz.basyskgt.configuration.PortConfiguration;
import org.eclipse.basyx.aas.manager.ConnectedAssetAdministrationShellManager;
import org.eclipse.basyx.aas.metamodel.connected.ConnectedAssetAdministrationShell;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class RegistrationStatusController {
    final BasyxInfrastructureConfig basyxInfrastructureConfig;
    final ConnectedAssetAdministrationShellManager aasManager;
    final PortConfiguration portConfiguration;
    private final Logger log = LoggerFactory.getLogger(RegistrationStatusController.class);
    private boolean[] registeredToAasRegistry;      // registration status of shells; registeredToAasRegistry[i]==true iff shell mapped to port i is registered
    private boolean[] shellUploadedToRepository;    // upload status of shells; shellUploadedToRepository[i]==true iff shell mapped to port i is uploaded


    @Autowired
    public RegistrationStatusController(BasyxInfrastructureConfig basyxInfrastructureConfig, ConnectedAssetAdministrationShellManager aasManager, PortConfiguration portConfiguration) {
        this.basyxInfrastructureConfig = basyxInfrastructureConfig;
        this.aasManager = aasManager;
        this.portConfiguration = portConfiguration;
        this.registeredToAasRegistry = new boolean[portConfiguration.getNumPorts()];
        this.shellUploadedToRepository = new boolean[portConfiguration.getNumPorts()];
    }

    public RegistrationStatus refreshAndGetStatus(int port) throws IllegalArgumentException {
        if (!portConfiguration.portExists(port)) {
            throw new IllegalArgumentException(String.format("Port number %d does not exist.", port));
        }
        refreshRegistrationStatus(port);
        return new RegistrationStatus(port, registeredToAasRegistry[port], shellUploadedToRepository[port]);
    }

    public RegistrationStatus[] refreshAndGetAllRegistrationStatus() {
        refreshRegistrationStatus();
        RegistrationStatus[] result = new RegistrationStatus[portConfiguration.getNumPorts()];
        for (int i = 0; i < result.length; i++) {
            result[i] = new RegistrationStatus(i, registeredToAasRegistry[i], shellUploadedToRepository[i]);
        }
        return result;
    }

    private void refreshRegistrationStatus() {
        for (int port = 0; port < portConfiguration.getNumPorts(); port++) {
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

    public record RegistrationStatus(int portNumber, boolean registeredToAasRegistry, boolean shellUploadedToRepository) {
    }
}
