package de.enbiz.basyskgt.storage;

import lombok.Data;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * A DTO for the {@link AasxFile} entity that contains only the files metadata but no content
 */
@Data
public class AasxFileMetadataDto implements Serializable {
    private final String id;                    // unique file id
    private final String name;
    private final String type;
    private final LocalDateTime timeCreated;
    private final IdentifierType identifierType;
    private final String identifier;      // identifier of the shell contained in the file

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AasxFileMetadataDto that = (AasxFileMetadataDto) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(type, that.type) && Objects.equals(timeCreated, that.timeCreated) && identifierType == that.identifierType && Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, type, timeCreated, identifierType, identifier);
    }
}