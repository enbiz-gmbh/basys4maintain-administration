package de.enbiz.basyskgt.configuration;

import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

/**
 * Keeps track of which Ballscrew is plugged into which input port of the device.
 * Ports are indexed from 0 to NUM_PORTS-1
 */
@Configuration
public class PortConfiguration {
    public static final int NUM_PORTS = 4;
    private final PortMappingRepository portMappingRepository;
    private Logger log = LoggerFactory.getLogger(PortConfiguration.class);

    public PortConfiguration(@Autowired PortMappingRepository portMappingRepository) {
        this.portMappingRepository = portMappingRepository;
    }

    /**
     * Maps a port to the device / Ballscrew plugged into it. This mapping is used to match incoming health values to
     * the physical device and AAS they belong to.
     *
     * @param portNumber port that the device is plugged in to
     * @param identifier {@link IIdentifier} of the device / AAS plugged in to that port
     * @throws IllegalArgumentException if the specified port does not exist or is already mapped to an Identifier. Or if the given identifier is already mapped to another port.
     */
    public void mapPort(int portNumber, IIdentifier identifier) throws IllegalArgumentException {
        log.info("Mapping port {} to identifier {}", portNumber, identifier.toString());
        if (portNumber >= NUM_PORTS || portNumber < 0) {
            throw new IllegalArgumentException(String.format("Port with index %d does not exist.", portNumber));
        }
        if (portMappingRepository.findByPortNumber(portNumber) != null) {
            throw new IllegalArgumentException(String.format("A mapping for port %d already exists.", portNumber));
        }
        if (portMappingRepository.findByIdentifierTypeAndIdentifierString(identifier.getIdType(), identifier.getId()) != null) {
            throw new IllegalArgumentException(String.format(
                    "The given Identifier is already mapped to port %d. Remove this mapping before remapping it to a new port.",
                    findPortForIdentifier(identifier))
            );
        }
        portMappingRepository.save(new PortMapping(portNumber, identifier));
    }

    /**
     * Removes the mapping for the specified port number. Needs to be called when a device is unplugged from a port.
     *
     * @param portNumber number of the port to have its mapping removed
     */
    @Transactional
    public void unmapPort(int portNumber) {
        log.info("Unmapping port {}", portNumber);
        if (portNumber >= NUM_PORTS || portNumber < 0) {
            throw new IllegalArgumentException(String.format("Port with index %d does not exist.", portNumber));
        }
        portMappingRepository.deleteByPortNumber(portNumber);
    }

    /**
     * Retrieve the {@link IIdentifier} for the device plugged into the given port.
     *
     * @param portNumber number of the port for which to get the mapping
     * @return IIdentifier of the device / AAS plugged into the given port. {@code null} if there is no device for that port
     * @throws IllegalArgumentException if the specified port does not exist
     */
    public IIdentifier getMappedIdentifier(int portNumber) throws IllegalArgumentException {
        if (portNumber >= NUM_PORTS || portNumber < 0) {
            throw new IllegalArgumentException(String.format("Port with index %d does not exist.", portNumber));
        }
        PortMapping mapping = portMappingRepository.findByPortNumber(portNumber);
        if (mapping == null) {
            return null;
        }
        return mapping.getIdentifier();
    }

    /**
     * For a given {@link IIdentifier}, find the port it is mapped to
     *
     * @param identifier IIdentifier for which to get the port number
     * @return number of the port the given identifier is mapped to; {@code null} if there is no mapping for the identifier
     */
    public Integer findPortForIdentifier(@NonNull IIdentifier identifier) {
        PortMapping mapping = portMappingRepository.findByIdentifierTypeAndIdentifierString(identifier.getIdType(), identifier.getId());
        if (mapping == null) {
            return null;
        }
        return mapping.getPortNumber();
    }
}
