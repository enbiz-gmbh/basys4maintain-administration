package de.enbiz.basyskgt.controller.rest;

import de.enbiz.basyskgt.configuration.PortConfiguration;
import de.enbiz.basyskgt.controller.HealthController;
import de.enbiz.basyskgt.model.HealthEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
public class HealthRestController {

    final HealthController healthController;
    final PortConfiguration portConfiguration;

    @Autowired
    public HealthRestController(HealthController healthController, PortConfiguration portConfiguration) {
        this.healthController = healthController;
        this.portConfiguration = portConfiguration;
    }

    /**
     * Transmit a new health value for the Ball Screw. Heath value will be forwarded to the AAS on the AAS-Server.
     *
     * @param health current health value as a percentage
     * @return if health value is > 100 or < 0: HTTP_BAD_REQUEST; else: HTTP_OK
     */
    @PostMapping("/api/health/{portNumber}")
    public ResponseEntity<String> updateHealth(@RequestBody short health, @PathVariable int portNumber) {
        try {
            healthController.setHealth(new HealthEntity(health, portConfiguration.getMappedIdentifier(portNumber)));
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.unprocessableEntity().body(e.getMessage());
        }
    }

    @GetMapping("/api/health")
    public ResponseEntity<List<HealthEntity>> getHealth(@RequestParam(defaultValue = "1") int count) {
        List<HealthEntity> result = healthController.getMostRecent(count);
        return ResponseEntity.ok(result);
    }
}
