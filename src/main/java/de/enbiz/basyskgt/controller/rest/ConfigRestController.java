package de.enbiz.basyskgt.controller.rest;

import de.enbiz.basyskgt.model.ConfigEntry;
import de.enbiz.basyskgt.model.ServerConfig;
import de.enbiz.basyskgt.persistence.ConfigParameter;
import de.enbiz.basyskgt.persistence.ConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Provides Methods for managing configuration of the Ball Screw
 */
@RestController
public class ConfigRestController {

    @Autowired
    ConfigRepository configRepository;

    @GetMapping("/api/config/{configParam}")
    public ConfigEntry getConfigParameter(@PathVariable ConfigParameter configParam) {
        return configRepository.getConfigParameter(configParam);
    }

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
