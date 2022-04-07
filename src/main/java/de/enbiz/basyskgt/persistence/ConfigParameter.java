package de.enbiz.basyskgt.persistence;

public enum ConfigParameter {

    AAS_SERVER_PATH("AAS Server Path", "URL of the AAS server"),
    REGISTRY_SERVER_PATH("Registry Server Path", "URL of the AAS registry");

    private final String displayName;
    private final String description;

    ConfigParameter(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
