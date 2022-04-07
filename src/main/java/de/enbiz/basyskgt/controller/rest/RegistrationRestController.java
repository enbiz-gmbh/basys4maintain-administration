package de.enbiz.basyskgt.controller.rest;

import de.enbiz.basyskgt.model.RegistrationStatus;
import de.enbiz.basyskgt.persistence.ConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegistrationRestController {

    @Autowired
    ConfigRepository configRepository;

    @GetMapping("/api/registration/register")
    public ResponseEntity<Void> register() {
        // TODO register to registry and repository
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @GetMapping("/api/registration/deregister")
    public ResponseEntity<Void> deregister() {
        // TODO deregister
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @GetMapping("/api/registration/status")
    public ResponseEntity<RegistrationStatus> getStatus() {
        // TODO
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
