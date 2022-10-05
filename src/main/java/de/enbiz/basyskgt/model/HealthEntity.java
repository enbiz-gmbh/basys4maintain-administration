package de.enbiz.basyskgt.model;

import java.time.LocalDateTime;
import java.util.Objects;


public class HealthEntity {
    private int remainingHealth;
    private LocalDateTime timeCreated;

    public HealthEntity() {
        this.timeCreated = LocalDateTime.now();
    }

    public HealthEntity(int remainingHealth) {
        this();
        this.remainingHealth = remainingHealth;
    }

    public int getRemainingHealth() {
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
        return remainingHealth == that.remainingHealth && Objects.equals(timeCreated, that.timeCreated);
    }

    @Override
    public int hashCode() {
        return Objects.hash(remainingHealth, timeCreated);
    }
}
