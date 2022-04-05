package de.enbiz.basyskgt.controller.rest;

import de.enbiz.basyskgt.model.ConfigParameter;
import de.enbiz.basyskgt.persistence.ConfigRepository;
import de.enbiz.basyskgt.model.ServerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ConfigRestController {

    @Autowired
    ConfigRepository configRepository;

    @GetMapping("/api/serverConfig")
    public ServerConfig getServerConfig() {
        return configRepository.getServerConfig();
    }

    @PostMapping("/api/serverConfig")
    public ResponseEntity<Void> setServerConfig(@RequestBody ServerConfig serverConfig) {
        configRepository.setServerConfig(serverConfig);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/api/configParam")
    public ResponseEntity<Void> putConfigParam(@RequestBody ConfigParameter configParameter) {
        configRepository.setConfigParameter(configParameter);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
