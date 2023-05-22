package de.enbiz.basyskgt.controller.rest;

import de.enbiz.basyskgt.configuration.PortConfiguration;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
    @Operation(summary = "Map a port to a file id", responses = {
            @ApiResponse(responseCode = "200", description = "successful operation"),
            @ApiResponse(responseCode = "422", description = "the given port can not be mapped to this file. Either the port or the file are already mapped. More details in response body.")
    })
    public ResponseEntity<Object> mapPort(@PathVariable int portNumber, @RequestBody String aasxFileID) {
        try {
            portConfiguration.mapPort(portNumber, aasxFileID);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.unprocessableEntity().body(e.getMessage());
        }
    }

    @GetMapping("/api/port/{portNumber}/aasId")
    @Operation(summary = "Get the device identifier mapped to a port", responses = {
            @ApiResponse(responseCode = "200", description = "successful operation"),
            @ApiResponse(responseCode = "404", description = "the given port does not exist")
    })
    public ResponseEntity<Object> getAasIdentifierForPort(@PathVariable int portNumber) {
        try {
            return ResponseEntity.ok(portConfiguration.getMappedAasIdentifier(portNumber));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/api/port/{portNumber}/fileId")
    @Operation(summary = "Get the device identifier mapped to a port", responses = {
            @ApiResponse(responseCode = "200", description = "successful operation"),
            @ApiResponse(responseCode = "404", description = "the given port does not exist")
    })
    public ResponseEntity<String> getFileIdForPort(@PathVariable int portNumber) {
        try {
            return ResponseEntity.ok(portConfiguration.getMappedFileId(portNumber));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/api/port/byFileId")
    @Operation(summary = "Get the port that a given file is mapped to", responses = {
            @ApiResponse(responseCode = "200", description = "successful operation"),
            @ApiResponse(responseCode = "404", description = "given fileID is not mapped to any port")
    })
    public ResponseEntity<Object> getPortByFileId(@RequestBody String aasxFileID) {
        Integer portNumber = portConfiguration.findPortForFile(aasxFileID);
        if (portNumber != null) {
            return ResponseEntity.ok(portNumber);
        } else {
            return ResponseEntity.notFound().build();
        }
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
