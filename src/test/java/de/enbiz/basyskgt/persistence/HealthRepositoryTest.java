package de.enbiz.basyskgt.persistence;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class HealthRepositoryTest {

    @Autowired
    HealthRepository healthRepository;
    Random random = new Random();

    @BeforeAll
    static void setup() {
        // delete database file before executing tests so a new DB is created at each test run
        File dbFile = new File("data/dbTest.mv.db");
        dbFile.delete();
    }

    @BeforeEach
    void beforeEach() {
        healthRepository.deleteAll();
    }

    @Test
    void saveDeleteCountFindAllTest() {
        int numEntries = random.nextInt(300) + 10; // we want to generate a random number of entries from 10 to 309
        List<HealthEntity> expected = new LinkedList<>();
        for (int i = 0; i < numEntries; i++) {
            HealthEntity healthEntity = healthRepository.save(new HealthEntity((short) random.nextInt(101)));
            expected.add(healthEntity);
            healthRepository.save(healthEntity);
        }

        Assertions.assertEquals(numEntries, healthRepository.count());
        Assertions.assertEquals(expected, healthRepository.findAll());

        long deletedId = random.nextInt(numEntries + 1);
        healthRepository.deleteById(deletedId);
        assertEquals(numEntries - 1, healthRepository.count());
    }

    @Test
    void findByTimeCreatedIsBetween() {
        // create entries that should NOT be in the result set
        for (int i = 0; i < 20; i++) {
            healthRepository.save(new HealthEntity((short) random.nextInt(101)));
        }

        // create entries that should be in the result set
        LocalDateTime start = LocalDateTime.now();
        List<HealthEntity> expected = new LinkedList<>();
        for (int i = 0; i < 20; i++) {
            HealthEntity healthEntity = healthRepository.save(new HealthEntity((short) random.nextInt(101)));
            expected.add(healthEntity);
            healthRepository.save(healthEntity);
        }
        LocalDateTime end = LocalDateTime.now();

        // create entries that should NOT be in the result set
        for (int i = 0; i < 20; i++) {
            healthRepository.save(new HealthEntity((short) random.nextInt(101)));
        }

        // query for result
        List<HealthEntity> actual = healthRepository.findByTimeCreatedIsBetween(start, end);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void findById() {
        // TODO
    }
}