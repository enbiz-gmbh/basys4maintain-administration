package de.enbiz.basyskgt.controller;

import de.enbiz.basyskgt.configuration.PortConfiguration;
import de.enbiz.basyskgt.dto.HealthDTO;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.eclipse.basyx.aas.manager.ConnectedAssetAdministrationShellManager;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

@Controller
public class HealthController {
    public static final int MAX_BUFFER_SIZE = 100;
    public static final String LATEST_HEALTH_VALUE = "LatestHealthValue";
    public static final IIdentifier HEALTH_SUBMODEL_IDENTIFIER = new Identifier(IdentifierType.IRI, "https://example.com/ids/sm/9130_7090_5032_9769");
    private final ConnectedAssetAdministrationShellManager aasManager;
    private final PortConfiguration portConfiguration;
    private final Logger log = LoggerFactory.getLogger(HealthController.class);
    private CircularFifoQueue<HealthDTO> buffer = new CircularFifoQueue<>(MAX_BUFFER_SIZE);


    @Autowired
    public HealthController(ConnectedAssetAdministrationShellManager aasManager, PortConfiguration portConfiguration) {
        this.aasManager = aasManager;
        this.portConfiguration = portConfiguration;
    }

    public void setHealth(int portNumber, float health) {
        log.info("Received health value for port {}: {}", portNumber, health);
        if (health < 0 || health > 100) {
            throw new IllegalArgumentException("Health value has to be a percentage between 0 and 100");
        }
        if (!portConfiguration.portExists(portNumber)) {
            throw new IllegalArgumentException(String.format("Port number %d does not exist.", portNumber));
        }
        if (portConfiguration.getMappedAasIdentifier(portNumber) == null) {
            throw new IllegalArgumentException("There is no device configured for the given port.");
        }
        IIdentifier aasIdentifier = portConfiguration.getMappedAasIdentifier(portNumber);

        log.debug("getting health submodel for port {} and setting {} to {}", portNumber, LATEST_HEALTH_VALUE, health);
        ISubmodel healthSubmodel = aasManager.retrieveSubmodel(aasIdentifier, HEALTH_SUBMODEL_IDENTIFIER);
        healthSubmodel.getSubmodelElement(LATEST_HEALTH_VALUE).setValue(health);

        buffer.add(new HealthDTO(health, aasIdentifier));
    }

    public HealthDTO getMostRecent() {
        return buffer.get(buffer.size() - 1);
    }

    public List<HealthDTO> getMostRecent(int count) {
        count = Math.min(count, size());
        if (count == size()) {
            return getAll();
        }
        return getAll().subList(size() - count, size());
    }

    public List<HealthDTO> getAll() {
        List<HealthDTO> result = new ArrayList<>(size());
        result.addAll(buffer);
        return result;
    }

    public void flush() {
        buffer = new CircularFifoQueue<>(MAX_BUFFER_SIZE);
    }

    public int size() {
        return buffer.size();
    }
}
