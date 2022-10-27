package de.enbiz.basyskgt.configuration;

import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class PortConfigurationTest {

    @Autowired
    PortConfiguration portConfiguration;
    Random random = new Random();

    @BeforeAll
    static void setup() {
        // delete database file before executing tests so a new DB is created at each test run
        File dbFile = new File("data/dbTest.mv.db");
        dbFile.delete();
    }

    @Test
    void mapPort() {
        assertTrue(portConfiguration.NUM_PORTS > 1, "There have to be at least two port enabled for the test to work");

        // mapping that should work
        int port = random.nextInt(portConfiguration.NUM_PORTS);
        IIdentifier identifier = new Identifier(IdentifierType.IRI, String.valueOf(random.nextLong()));
        portConfiguration.mapPort(port, identifier);

        // map port that has already been mapped
        IIdentifier identifier2 = new Identifier(IdentifierType.IRI, String.valueOf(random.nextLong()));
        assertThrows(IllegalArgumentException.class, () -> portConfiguration.mapPort(port, identifier2));

        // map identifier that has already been mapped
        int port2 = port == 0 ? port + 1 : port - 1;
        assertThrows(IllegalArgumentException.class, () -> portConfiguration.mapPort(port2, identifier));

        // map ports that do not exist
        assertThrows(IllegalArgumentException.class, () -> portConfiguration.mapPort(portConfiguration.NUM_PORTS, identifier));
        assertThrows(IllegalArgumentException.class, () -> portConfiguration.mapPort(-2, identifier));


    }

    @Test
    void unmapPort() {
        // TODO
    }

    @Test
    void getMappedIdentifier() {
        // TODO
    }

    @Test
    void findPortForIdentifier() {
        // TODO
    }
}