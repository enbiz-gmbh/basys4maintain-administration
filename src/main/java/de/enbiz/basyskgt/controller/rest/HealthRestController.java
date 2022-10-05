package de.enbiz.basyskgt.controller.rest;

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

    @Autowired
    public HealthRestController(HealthController healthController) {
        this.healthController = healthController;
    }

    /**
     * Transmit a new health value for the Ball Screw. Heath value will be forwarded to the AAS on the AAS-Server.
     *
     * @param health current health value as a percentage
     * @return if health value is > 100 or < 0: HTTP_BAD_REQUEST; else: HTTP_OK
     */
    @PostMapping("/api/health")
    public ResponseEntity<String> updateHealth(@RequestBody short health) {
        try {
            healthController.setHealth(health);
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
