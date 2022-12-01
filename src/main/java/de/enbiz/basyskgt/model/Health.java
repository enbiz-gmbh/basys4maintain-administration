package de.enbiz.basyskgt.model;

import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;

import java.time.LocalDateTime;
import java.util.Objects;


/**
 * Object containing a health value, the {@link IIdentifier} of the device it belongs to and the time at which the health value was calculated.
 */
public class Health {
    private final int health;
    private final LocalDateTime time;
    private final IIdentifier identifier;

    public Health(int health, IIdentifier identifier) {
        this.identifier = identifier;
        this.time = LocalDateTime.now();
        this.health = health;
    }

    public int getHealth() {
        return health;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public IIdentifier getIdentifier() {
        return identifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Health that = (Health) o;
        return health == that.health && Objects.equals(time, that.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(health, time);
    }
}