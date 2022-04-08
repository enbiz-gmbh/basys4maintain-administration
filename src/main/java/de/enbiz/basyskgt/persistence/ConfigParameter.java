package de.enbiz.basyskgt.persistence;

public enum ConfigParameter {

    AAS_SERVER_PATH("AAS Server Path", "URL of the AAS server", "http://<AAS-SERVER-IP>:4001/shells"),
    REGISTRY_SERVER_PATH("Registry Server Path", "URL of the AAS registry", "http://<REGISTRY-IP>:4000/registry"),
    LOCAL_REGISTRY_SERVER_PORT("Local Registry Server Port", "The port at which the integrated BaSys registry server should be hosted if enabled", "4000"),
    LOCAL_REGISTRY_SERVER_PATH("Local Registry Server Path", "The context path at which the integrated BaSys registry server should be hosted if enabled", "/registry"),
    LOCAL_AAS_SERVER_PORT("Local AAS Server Port", "The port at which the integrated BaSys AAS server should be hosted if enabled", "4001"),
    LOCAL_AAS_SERVER_PATH("Local AAS Server Path", "The context path at which the integrated BaSys AAS server should be hosted if enabled", "shells"),
    LOCAL_REGISTRY_AND_AAS_SERVER_ENABLED("Enable Local Registry and AAS Server", "If enabled a dedicated AAS-Server and Registry will be hosted by the application. Use this option if you do not already have a BaSys infrastructure in place", "false");

    private final String displayName;
    private final String description;
    private final String defaultValue;

    ConfigParameter(String displayName, String description, String defaultValue) {
        this.displayName = displayName;
        this.description = description;
        this.defaultValue = defaultValue;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
