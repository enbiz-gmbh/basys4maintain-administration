package de.enbiz.basyskgt.persistence;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "HEALTH")
public class HealthEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "remainingHealth", nullable = false)
    private short remainingHealth;

    @Column(name = "timeCreated", nullable = false)
    private LocalDateTime timeCreated;

    public HealthEntity() {
        this.timeCreated = LocalDateTime.now();
    }

    public HealthEntity(short remainingHealth) {
        this();
        this.remainingHealth = remainingHealth;
    }

    public Long getId() {
        return id;
    }

    public short getRemainingHealth() {
        return remainingHealth;
    }

    public LocalDateTime getTimeCreated() {
        return timeCreated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HealthEntity that = (HealthEntity) o;
        return remainingHealth == that.remainingHealth && Objects.equals(id, that.id) && Objects.equals(timeCreated, that.timeCreated);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, remainingHealth, timeCreated);
    }
}
