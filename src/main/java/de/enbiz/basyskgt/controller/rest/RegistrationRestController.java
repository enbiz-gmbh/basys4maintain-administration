package de.enbiz.basyskgt.controller.rest;

import de.enbiz.basyskgt.controller.RegistrationController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class RegistrationRestController {

    private static final Logger log = LoggerFactory.getLogger(RegistrationRestController.class);

    final RegistrationController registrationController;

    @Autowired
    public RegistrationRestController(RegistrationController registrationController) {
        this.registrationController = registrationController;
    }

    @GetMapping("/api/registration/register")
    @Operation(summary = "register AAS to the registry and upload to AAS server", responses = {
            @ApiResponse(responseCode = "200", description = "successful operation"),
            @ApiResponse(responseCode = "409", description = "Already registered and uploaded")
    })
    public ResponseEntity<String> register() {
        try {
            registrationController.register();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/registration/deregister")
    @Operation(summary = "deregister AAS from the registry and delete from AAS server", responses = {
            @ApiResponse(responseCode = "200", description = "successful operation"),
            @ApiResponse(responseCode = "409", description = "Not currently registered or uploaded")
    })
    public ResponseEntity<String> deregister() {
        try {
            registrationController.deregister();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/registration/status")
    @Operation(summary = "get current registration and upload status", responses = {
            @ApiResponse(responseCode = "200", description = "successful operation")
    })
    public ResponseEntity<RegistrationController.RegistrationStatusDAO> getRegistrationStatus() {
        return ResponseEntity.ok(registrationController.getStatus());
    }

}
