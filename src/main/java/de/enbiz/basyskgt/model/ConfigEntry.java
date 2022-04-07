package de.enbiz.basyskgt.model;

import de.enbiz.basyskgt.persistence.ConfigParameter;

public class ConfigEntry {
    private final ConfigParameter id;
    private final String value;

    public ConfigEntry(ConfigParameter id, String value) {
        this.id = id;
        this.value = value;
    }

    public ConfigParameter getId() {
        return id;
    }

    public String getValue() {
        return value;
    }
}
