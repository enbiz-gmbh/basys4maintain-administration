package de.enbiz.basyskgt.controller.storage;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * A DTO for the {@link DbFile} entity that contains only the files metadata but no content
 */
@Data
public class DbFileMetadataDto implements Serializable {
    private final String id;
    private final String name;
    private final String type;
    private final LocalDateTime timeCreated;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DbFileMetadataDto that = (DbFileMetadataDto) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(type, that.type) && Objects.equals(timeCreated, that.timeCreated);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, type, timeCreated);
    }
}