package de.enbiz.basyskgt.persistence;

import de.enbiz.basyskgt.controller.HealthController;
import de.enbiz.basyskgt.model.HealthEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

@SpringBootTest
class HealthRepositoryTest {

    @Autowired
    HealthController healthController;
    Random random = new Random();

    @BeforeAll
    static void setup() {
        // delete database file before executing tests so a new DB is created at each test run
        File dbFile = new File("data/dbTest.mv.db");
        dbFile.delete();
    }

    @BeforeEach
    void beforeEach() {
        healthController.flush();
    }

    @Test
    void saveCountFindAllTest() {
        int numEntries = random.nextInt(10, HealthController.MAX_BUFFER_SIZE);
        List<HealthEntity> expected = new LinkedList<>();
        for (int i = 0; i < numEntries; i++) {
            HealthEntity healthEntity = new HealthEntity(random.nextInt(101));
            expected.add(healthEntity);
            healthController.setHealth(healthEntity);
        }

        Assertions.assertEquals(numEntries, healthController.size());
        Assertions.assertEquals(expected, healthController.getMostRecent(numEntries));
    }
}