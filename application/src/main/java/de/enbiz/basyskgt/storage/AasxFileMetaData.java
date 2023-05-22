package de.enbiz.basyskgt.storage;

import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;

import java.time.LocalDateTime;

/**
 * A Projection for the {@link AasxFile} entity
 */
public interface AasxFileMetaData {
    String getId();

    String getName();

    String getType();

    LocalDateTime getTimeCreated();

    IdentifierType getIdentifierType();

    String getIdentifier();
}