package de.enbiz.basyskgt.controller.rest;

import de.enbiz.basyskgt.model.ConfigParameter;
import de.enbiz.basyskgt.model.ServerConfig;
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

    @PostMapping("/api/config")
    public ResponseEntity<Void> postConfigParam(@RequestBody ConfigParameter configParameter) {
        configRepository.setConfigParameter(configParameter);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/api/config/{configParamId}")
    public ConfigParameter getConfigParameter(@PathVariable String configParamId) {
        return configRepository.getConfigParameter(configParamId);
    }

    @PutMapping("/api/config/{configParamId}")
    public ResponseEntity<Void> putConfigParam(@RequestBody ConfigParameter configParameter, @PathVariable String configParamId) {
        configRepository.setConfigParameter(configParameter);
        return new ResponseEntity<>(HttpStatus.OK);
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
}
