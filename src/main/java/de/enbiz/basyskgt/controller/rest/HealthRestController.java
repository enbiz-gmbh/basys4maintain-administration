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
        // TODO send value to BaSyx
        if (health < 0 || health > 100) {
            return new ResponseEntity<>("Health value may not be < 0 or > 100", HttpStatus.BAD_REQUEST);
        }
        healthController.insert(health);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/api/health")
    public ResponseEntity<List<HealthEntity>> getHealth(@RequestParam(defaultValue = "1") int count) {
        List<HealthEntity> result = healthController.getMostRecent(count);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
