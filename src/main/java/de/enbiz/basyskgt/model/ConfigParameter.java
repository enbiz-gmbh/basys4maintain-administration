package de.enbiz.basyskgt.model;

public class ConfigParameter {
    private final String id;
    private final String value;

    public ConfigParameter(String id, String value) {
        this.id = id;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public String getValue() {
        return value;
    }
}
