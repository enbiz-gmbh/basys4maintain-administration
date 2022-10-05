package de.enbiz.basyskgt.controller.rest;

import de.enbiz.basyskgt.controller.HealthController;
import de.enbiz.basyskgt.model.HealthEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @GetMapping("/api/health")
    public ResponseEntity<List<HealthEntity>> getHealth(@RequestParam(defaultValue = "1") int count) {
        List<HealthEntity> result = healthController.getMostRecent(count);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
