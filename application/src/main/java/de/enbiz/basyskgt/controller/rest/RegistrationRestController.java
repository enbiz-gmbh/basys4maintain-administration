package de.enbiz.basyskgt.controller.rest;

import de.enbiz.basyskgt.controller.RegistrationController;
import de.enbiz.basyskgt.controller.RegistrationStatusController;
import de.enbiz.basyskgt.exceptions.AASXFileParseException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class RegistrationRestController {

    private static final Logger log = LoggerFactory.getLogger(RegistrationRestController.class);

    final RegistrationController registrationController;
    final RegistrationStatusController registrationStatusController;

    @Autowired
    public RegistrationRestController(RegistrationController registrationController, RegistrationStatusController registrationStatusController) {
        this.registrationController = registrationController;
        this.registrationStatusController = registrationStatusController;
    }

    @GetMapping("/api/registration/register")
    @Operation(summary = "register AAS to the registry and upload to AAS server", responses = {
            @ApiResponse(responseCode = "200", description = "successful operation"),
            @ApiResponse(responseCode = "409", description = "Already registered and uploaded"),
            @ApiResponse(responseCode = "400", description = "Port does not exist"),
            @ApiResponse(responseCode = "500", description = "internal server error")
    })
    public ResponseEntity<String> register(@RequestParam int port) {
        boolean success = false;
        try {
            success = registrationController.register(port);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (InvalidFormatException e) {
            return ResponseEntity.internalServerError().body("AASX file contains more than one AAS");
        } catch (AASXFileParseException e) {
            log.error("Exception occured while convertiong AASX file to AASBundle", e);
            return ResponseEntity.internalServerError().body("AASX file could not be read");
        }
        if (success) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.internalServerError().body("Unknown server error occurred");
        }
    }

    @GetMapping("/api/registration/deregister")
    @Operation(summary = "deregister AAS from the registry and delete from AAS server", responses = {
            @ApiResponse(responseCode = "200", description = "successful operation"),
            @ApiResponse(responseCode = "409", description = "Not currently registered or uploaded"),
            @ApiResponse(responseCode = "400", description = "Port does not exist"),
            @ApiResponse(responseCode = "500", description = "internal server error")
    })
    public ResponseEntity<String> deregister(@RequestParam int port) {
        try {
            registrationController.deregister(port);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (InvalidFormatException e) {
            return ResponseEntity.internalServerError().body("AASX file contains more than one AAS");
        } catch (AASXFileParseException e) {
            log.error("Exception occured while convertiong AASX file to AASBundle", e);
            return ResponseEntity.internalServerError().body("AASX file could not be read");
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/registration/status")
    @Operation(summary = "get current registration and upload status for a port. If no Port number is supplied, registration status for all ports is returned", responses = {
            @ApiResponse(responseCode = "200", description = "successful operation")
    })
    public ResponseEntity<?> getRegistrationStatus(@RequestParam(required = false) Integer port) {
        RegistrationStatusController.RegistrationStatus[] result;
        if (port != null) {
            try {
                result = new RegistrationStatusController.RegistrationStatus[]{registrationStatusController.refreshAndGetStatus(port)};
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        } else {
            result = registrationStatusController.refreshAndGetAllRegistrationStatus();
        }

        return ResponseEntity.ok(result);
    }

}
