package de.enbiz.basyskgt.configuration;

import de.enbiz.basyskgt.storage.AasxFile;
import de.enbiz.basyskgt.storage.AasxFileRepository;
import lombok.Getter;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Keeps track of which Ballscrew is plugged into which input port of the device.
 * Ports are indexed from 0 to NUM_PORTS-1
 */
@Configuration
public class PortConfiguration {

    @Getter
    private int numPorts;
    private final PortMappingRepository portMappingRepository;
    private final AasxFileRepository aasxFileRepository;
    private final Logger log = LoggerFactory.getLogger(PortConfiguration.class);

    @Autowired
    public PortConfiguration(@Value("${basyx.local.portcount}") int numPorts,
                             PortMappingRepository portMappingRepository,
                             AasxFileRepository aasxFileRepository) {
        this.numPorts = numPorts;
        this.portMappingRepository = portMappingRepository;
        this.aasxFileRepository = aasxFileRepository;
    }

    /**
     * Maps a port to the device / Ballscrew plugged into it. This mapping is used to match incoming health values to
     * the physical device and AAS they belong to.
     *
     * @param portNumber port that the device is plugged in to
     * @param aasxFileID id of the {@link de.enbiz.basyskgt.storage.AasxFile} for the device plugged in to that port
     * @throws IllegalArgumentException if the specified port does not exist or is already mapped. Or if the given file id is already mapped to another port.
     */
    public void mapPort(int portNumber, String aasxFileID) throws IllegalArgumentException {
        log.info("Mapping port {} to aasx file {}", portNumber, aasxFileID);

        // check if port number exists
        if (!portExists(portNumber)) {
            throw new IllegalArgumentException(String.format("Port with index %d does not exist.", portNumber));
        }

        // check if file exists
        Optional<AasxFile> optionalAasxFile = aasxFileRepository.findById(aasxFileID);
        if (optionalAasxFile.isEmpty()) {
            throw new IllegalArgumentException(String.format("No aasx file with id %s exists. You need to upload the file first.", aasxFileID));
        }

        // check if port is already mapped
        if (portMappingRepository.findByPortNumber(portNumber) != null) {
            throw new IllegalArgumentException(String.format("A mapping for port %d already exists.", portNumber));
        }

        // check if file is already mapped to a port
        if (portMappingRepository.findByAasxFile_Id(aasxFileID) != null) {
            throw new IllegalArgumentException(String.format(
                    "The given aasx file is already mapped to port %d. Remove this mapping before remapping it to a new port.",
                    findPortForFile(aasxFileID))
            );
        }

        PortMapping portMapping = new PortMapping(portNumber);
        portMapping.setAasxFile(optionalAasxFile.get());
        portMappingRepository.save(portMapping);
    }

    public boolean portExists(int portNumber) {
        return portNumber < numPorts && portNumber >= 0;
    }

    /**
     * Removes the mapping for the specified port number. Needs to be called when a device is unplugged from a port.
     *
     * @param portNumber number of the port to have its mapping removed
     */
    @Transactional
    public void unmapPort(int portNumber) {
        // TODO check if port is currently registered. Only allow unmapping if not registered.
        log.info("Unmapping port {}", portNumber);
        if (portNumber >= numPorts || portNumber < 0) {
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
    public IIdentifier getMappedAasIdentifier(int portNumber) throws IllegalArgumentException {
        if (portNumber >= numPorts || portNumber < 0) {
            throw new IllegalArgumentException(String.format("Port with index %d does not exist.", portNumber));
        }
        PortMapping mapping = portMappingRepository.findByPortNumber(portNumber);
        if (mapping == null) {
            return null;
        }
        return new Identifier(mapping.getAasxFile().getIdentifierType(), mapping.getAasxFile().getIdentifier());
    }

    /**
     * Retrieve the id of the {@link AasxFile} mapped to the given port
     *
     * @param portNumber number of the port for which to get the file id
     * @return unique file identifier mapped to the given port. {@code null} if there is no device for that port
     * @throws IllegalArgumentException if the specified port does not exist
     */
    public String getMappedFileId(int portNumber) throws IllegalArgumentException {
        return getMappedFile(portNumber).getId();
    }

    /**
     * Retrieve the {@link AasxFile} mapped to the given port
     *
     * @param portNumber number of the port for which to get the file id
     * @return Aasx file mapped to that port
     * @throws IllegalArgumentException if the specified port does not exist
     */
    public AasxFile getMappedFile(int portNumber) throws IllegalArgumentException {
        if (portNumber >= numPorts || portNumber < 0) {
            throw new IllegalArgumentException(String.format("Port with index %d does not exist.", portNumber));
        }
        PortMapping mapping = portMappingRepository.findByPortNumber(portNumber);
        if (mapping == null) {
            return null;
        }
        return mapping.getAasxFile();
    }

    /**
     * For a given {@link de.enbiz.basyskgt.storage.AasxFile}, find the port it is mapped to
     *
     * @param aasxFileID id of the AasxFile for which to get the port number
     * @return number of the port the given file is mapped to; {@code null} if there is no mapping for the file
     */
    public Integer findPortForFile(@NonNull String aasxFileID) {
        PortMapping mapping = portMappingRepository.findByAasxFile_Id(aasxFileID);
        if (mapping == null) {
            return null;
        }
        return mapping.getPortNumber();
    }
}
