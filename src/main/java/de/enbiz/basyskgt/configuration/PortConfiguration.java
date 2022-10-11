package de.enbiz.basyskgt.configuration;

import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Keeps track of which Ballscrew is plugged into which input port of the device.
 * Ports are indexed from 0 to NUM_PORTS-1
 */
@Configuration
public class PortConfiguration {
    public static final int NUM_PORTS = 4;

    private Map<Integer, IIdentifier> portMappings = new HashMap<>(NUM_PORTS);

    /**
     * Maps a port to the device / Ballscrew plugged into it. This mapping is used to match incoming health values to
     * the physical device and AAS they belong to.
     *
     * @param portNumber port that the device is plugged in to
     * @param identifier {@link IIdentifier} of the device / AAS plugged in to that port
     * @throws IllegalArgumentException if the specified port does not exist or is already mapped to an Identifier. Or if the given identifier is already mapped to another port.
     */
    public void mapPort(int portNumber, IIdentifier identifier) throws IllegalArgumentException {
        if (portNumber >= NUM_PORTS) {
            throw new IllegalArgumentException(String.format("Port with index %d does not exist.", portNumber));
        }
        if (portMappings.get(portNumber) != null) {
            throw new IllegalArgumentException(String.format("A mapping for port %d already exists.", portNumber));
        }
        if (portMappings.containsValue(identifier)) {
            throw new IllegalArgumentException(String.format(
                    "The given Identifier is already mapped to port %d. Remove this mapping before remapping it to a new port.",
                    findPortForIdentifier(identifier))
            );
        }
        portMappings.put(portNumber, identifier);
    }

    /**
     * Removes the mapping for the specified port number. Needs to be called when a device is unplugged from a port.
     *
     * @param portNumber number of the port to have its mapping removed
     * @return true if the port was mapped to a value; false otherwise
     */
    public boolean unmapPort(int portNumber) {
        return portMappings.remove(portNumber) != null;
    }

    /**
     * Retrieve the {@link IIdentifier} for the device plugged into the given port.
     *
     * @param portNumber number of the port for which to get the mapping
     * @return IIdentifier of the device / AAS plugged into the given port. {@code null} if there is no device for that port
     * @throws IllegalArgumentException if the specified port does not exist
     */
    public IIdentifier getMappedIdentifier(int portNumber) throws IllegalArgumentException {
        if (portNumber >= NUM_PORTS) {
            throw new IllegalArgumentException(String.format("Port with index %d does not exist.", portNumber));
        }
        return portMappings.get(portNumber);
    }

    /**
     * For a given {@link IIdentifier}, find the port it is mapped to
     *
     * @param identifier IIdentifier for which to get the port number
     * @return number of the port the given identifier is mapped to; -1 if there is no mapping for the identifier
     */
    public int findPortForIdentifier(@NonNull IIdentifier identifier) {
        for (Map.Entry<Integer, IIdentifier> e : portMappings.entrySet()) {
            if (identifier.equals(e.getValue())) {
                return e.getKey();
            }
        }
        return -1;
    }
}
