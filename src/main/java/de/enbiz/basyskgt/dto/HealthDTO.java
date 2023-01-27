package de.enbiz.basyskgt.dto;

import lombok.Getter;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;

import java.time.LocalDateTime;
import java.util.Objects;


/**
 * DTO for health values.
 * Contains the {@link IIdentifier} of the device it belongs to and the time at which the health value was calculated.
 */
@Getter
public class HealthDTO {
    private final int health;
    private final LocalDateTime time;
    private final IIdentifier identifier;

    public HealthDTO(int health, IIdentifier identifier) {
        this.identifier = identifier;
        this.time = LocalDateTime.now();
        this.health = health;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HealthDTO that = (HealthDTO) o;
        return health == that.health && Objects.equals(time, that.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(health, time);
    }
}
