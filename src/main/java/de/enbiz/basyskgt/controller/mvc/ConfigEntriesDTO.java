package de.enbiz.basyskgt.controller.mvc;

import de.enbiz.basyskgt.model.ConfigEntry;

import java.util.LinkedList;
import java.util.List;

public class ConfigEntriesDTO {
    List<ConfigEntry> configEntries;

    public ConfigEntriesDTO() {
        this.configEntries = new LinkedList<>();
    }

    public void addConfigEntry(ConfigEntry configEntry) {
        this.configEntries.add(configEntry);
    }

    public List<ConfigEntry> getConfigEntries() {
        return configEntries;
    }

    @Override
    public String toString() {
        return "ConfigEntriesDTO{" +
                "configEntries=" + configEntries +
                '}';
    }
}
