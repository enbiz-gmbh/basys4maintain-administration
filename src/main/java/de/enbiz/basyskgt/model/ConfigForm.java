package de.enbiz.basyskgt.model;

import de.enbiz.basyskgt.persistence.ConfigParameter;

import java.util.HashMap;
import java.util.Map;

public class ConfigForm {
    Map<ConfigParameter, String> configEntries;

    public ConfigForm() {
        this.configEntries = new HashMap<>();
    }

    public void addConfigEntry(ConfigEntry configEntry) {
        this.configEntries.put(configEntry.getId(), configEntry.getValue());
    }

    public Map<ConfigParameter, String> getConfigEntries() {
        return configEntries;
    }

    @Override
    public String toString() {
        return "ConfigForm{" +
                "configEntries=" + configEntries +
                '}';
    }
}
