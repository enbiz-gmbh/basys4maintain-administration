package de.enbiz.basyskgt.controller.rest;

import de.enbiz.basyskgt.persistence.HealthEntity;
import de.enbiz.basyskgt.persistence.HealthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthRestController {

    @Autowired
    HealthRepository healthRepository;

    @PostMapping("/api/health")
    public ResponseEntity<String> updateHealth(@RequestBody short health) {
        if (health < 0 || health > 100) {
            return new ResponseEntity<>("Health value may not be < 0 or > 100", HttpStatus.BAD_REQUEST);
        }
        HealthEntity healthEntity = new HealthEntity(health);
        healthRepository.save(healthEntity);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/api/health")
    public ResponseEntity<HealthEntity> getHealth() {
        HealthEntity result = healthRepository.findFirstByOrderByIdDesc();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
