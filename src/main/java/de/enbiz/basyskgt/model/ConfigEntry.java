package de.enbiz.basyskgt.model;

import de.enbiz.basyskgt.persistence.ConfigParameter;

import java.util.Objects;

public class ConfigEntry {
    private final ConfigParameter id;
    private String value;

    public ConfigEntry(ConfigParameter id, String value) {
        this.id = id;
        this.value = value;
    }

    public ConfigParameter getId() {
        return id;
    }

    public String getValue() {
        return (value == null) ? "" : value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfigEntry that = (ConfigEntry) o;
        return id == that.id && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, value);
    }
}
