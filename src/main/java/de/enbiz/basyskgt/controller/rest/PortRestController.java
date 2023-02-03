package de.enbiz.basyskgt.controller.rest;

import de.enbiz.basyskgt.configuration.PortConfiguration;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    @Operation(summary = "Map a port to a device identifier", responses = {
            @ApiResponse(responseCode = "200", description = "successful operation"),
            @ApiResponse(responseCode = "422", description = "the given port can not be mapped to this identifier. Either the port or the identifier are already mapped. More details in response body.")
    })
    public ResponseEntity<Object> mapPort(@PathVariable int portNumber, @RequestBody IIdentifier identifier) {
        try {
            portConfiguration.mapPort(portNumber, identifier);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.unprocessableEntity().body(e.getMessage());
        }
    }

    @GetMapping("/api/port/{portNumber}")
    @Operation(summary = "Get the device identifier mapped to a port", responses = {
            @ApiResponse(responseCode = "200", description = "successful operation"),
            @ApiResponse(responseCode = "404", description = "the given port does not exist")
    })
    public ResponseEntity<Object> getIdentifierForPort(@PathVariable int portNumber) {
        try {
            return ResponseEntity.ok(portConfiguration.getMappedIdentifier(portNumber));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/api/port")
    @Operation(summary = "Get the port that a given identifier is mapped to", responses = {
            @ApiResponse(responseCode = "200", description = "successful operation")
    })
    public ResponseEntity<Object> getPortForIdentifier(@RequestBody IIdentifier identifier) {
        return ResponseEntity.ok(portConfiguration.findPortForIdentifier(identifier));
    }

    @DeleteMapping("/api/port/{portNumber}")
    @Operation(summary = "Unmap given port", responses = {
            @ApiResponse(responseCode = "200", description = "successful operation"),
            @ApiResponse(responseCode = "404", description = "the given port does not exist")
    })
    public ResponseEntity<String> unmapPort(@PathVariable int portNumber) {
        try {
            portConfiguration.unmapPort(portNumber);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
