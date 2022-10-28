package de.enbiz.basyskgt.controller.rest;

import de.enbiz.basyskgt.configuration.PortConfiguration;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class PortRestController {

    private final PortConfiguration portConfiguration;

    @Autowired
    public PortRestController(PortConfiguration portConfiguration) {
        this.portConfiguration = portConfiguration;
    }

    @PostMapping("/api/port/{portNumber}")
    public ResponseEntity<Object> mapPort(@PathVariable int portNumber, @RequestBody IIdentifier identifier) {
        try {
            portConfiguration.mapPort(portNumber, identifier);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.unprocessableEntity().body(e.getMessage());
        }
    }

    @GetMapping("/api/port/{portNumber}")
    public ResponseEntity<Object> getIdentifierForPort(@PathVariable int portNumber) {
        try {
            return ResponseEntity.ok(portConfiguration.getMappedIdentifier(portNumber));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/api/port")
    public ResponseEntity<Object> getPortOrIdentifier(@RequestBody IIdentifier identifier) {
        try {
            return ResponseEntity.ok(portConfiguration.findPortForIdentifier(identifier));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/api/port/{portNumber}")
    public ResponseEntity<String> unmapPort(@PathVariable int portNumber) {
        try {
            portConfiguration.unmapPort(portNumber);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
