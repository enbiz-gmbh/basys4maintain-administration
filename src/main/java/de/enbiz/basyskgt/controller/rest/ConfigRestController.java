package de.enbiz.basyskgt.controller.rest;

import de.enbiz.basyskgt.model.ConfigEntry;
import de.enbiz.basyskgt.model.ServerConfig;
import de.enbiz.basyskgt.persistence.ConfigParameter;
import de.enbiz.basyskgt.persistence.ConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * Provides Methods for managing configuration of the Ball Screw
 */
@RestController
public class ConfigRestController {

    @Autowired
    ConfigRepository configRepository;

    /**
     * Provides a list of all the {@link ConfigParameter} IDs that exist. The result only contains the respective IDs / names
     * for a full serialization including displayName, description, etc. see {@link #getConfigParameterInfo(ConfigParameter)}.
     *
     * @return the complete list of available ConfigParameters
     */
    @GetMapping("/api/config/parametersList")
    public List<ConfigParameter> getConfigParametersList() {
        return Arrays.asList(ConfigParameter.values());
    }

    /**
     * Provides a {@link ConfigParameter.ConfigParameterInfo} containing all the additional info about the given
     * {@link ConfigParameter} like displayName, Description and defaultValue.
     *
     * @param configParam the {@link ConfigParameter} to retrieve the info for
     * @return Object containing all the info about the given ConfigParameter.
     */
    @GetMapping("/api/config/{configParam}/info")
    public ConfigParameter.ConfigParameterInfo getConfigParameterInfo(@PathVariable ConfigParameter configParam) {
        return configParam.getInfo();
    }

    /**
     * Retrieves the configured Value of a given {@link ConfigParameter}
     *
     * @param configParam The {@link ConfigParameter} for which to retrieve the saved {@link ConfigEntry}
     * @return A {@link ConfigEntry} containing the given {@link ConfigParameter} and the value it is set to
     */
    @GetMapping("/api/config/{configParam}")
    public ConfigEntry getConfigEntry(@PathVariable ConfigParameter configParam) {
        return configRepository.getConfigEntry(configParam);
    }

    /**
     * Update or set the value of a {@link ConfigParameter}
     *
     * @param configEntry The {@link ConfigEntry} containing the name of the {@link ConfigParameter} to be updated and its new value
     * @param configParam The name of the {@link ConfigParameter} to be updated. This has to match the given ConfigEntry
     * @return The {@link HttpStatus} of the request and an error message if something went wrong
     */
    @PutMapping("/api/config/{configParam}")
    public ResponseEntity<String> putConfigParam(@RequestBody ConfigEntry configEntry, @PathVariable ConfigParameter configParam) {
        if (!configParam.equals(configEntry.getId())) {
            // put-location does not match configParamId in request payload
            return new ResponseEntity<>("Parameter 'configParamId' in request URI does not match 'configParamId of the payload.", HttpStatus.BAD_REQUEST);
        } else {
            configRepository.setConfigParameter(configEntry);
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    @GetMapping("/api/config/serverConfig")
    public ServerConfig getServerConfig() {
        return configRepository.getServerConfig();
    }

    @PutMapping("/api/config/serverConfig")
    public ResponseEntity<Void> setServerConfig(@RequestBody ServerConfig serverConfig) {
        configRepository.setServerConfig(serverConfig);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/api/config/reset")
    public ResponseEntity<Void> resetConfig() {
        configRepository.resetConfig();
        return new ResponseEntity<>(HttpStatus.OK);

    }
}
