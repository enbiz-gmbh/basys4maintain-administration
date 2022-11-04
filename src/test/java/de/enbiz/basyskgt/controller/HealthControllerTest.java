package de.enbiz.basyskgt.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;

@SpringBootTest
class HealthControllerTest {

    @Autowired
    HealthController healthController;

    @BeforeAll
    static void beforeAll() {
        // delete database file before executing tests so a new DB is created at each test run
        File dbFile = new File("data/dbTest.mv.db");
        dbFile.delete();
    }

    @Test
    void setHealth() {
        // TODO implement test case
    }

    @Test
    void getMostRecent() {
        // TODO implement test case
    }

    @Test
    void testGetMostRecent() {
        // TODO implement test case
    }

    @Test
    void getAll() {
        // TODO implement test case
    }

    @Test
    void flush() {
        // TODO implement test case
    }

    @Test
    void size() {
        // TODO implement test case
    }
}