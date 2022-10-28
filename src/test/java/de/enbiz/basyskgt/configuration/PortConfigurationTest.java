package de.enbiz.basyskgt.configuration;

import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PortConfigurationTest {

    @Autowired
    PortConfiguration portConfiguration;

    @BeforeAll
    static void beforeAll() {
        // delete database file before executing tests so a new DB is created at each test run
        File dbFile = new File("data/dbTest.mv.db");
        dbFile.delete();
    }

    @BeforeEach
    void beforeEach(@Autowired PortMappingRepository portMappingRepository) {
        portMappingRepository.deleteAll();
    }

    @Test
    void mapPort() {
        assertTrue(PortConfiguration.NUM_PORTS > 1, "There have to be at least two port enabled for the test to work");

        // mapping that should work
        int port1 = 0;
        IIdentifier identifier1 = new Identifier(IdentifierType.IRI, "identifier1");
        portConfiguration.mapPort(port1, identifier1);

        // map port that has already been mapped
        IIdentifier identifier2 = new Identifier(IdentifierType.IRI, "identifier2");
        assertThrows(IllegalArgumentException.class, () -> portConfiguration.mapPort(port1, identifier2));

        // map identifier that has already been mapped
        int port2 = 1;
        assertThrows(IllegalArgumentException.class, () -> portConfiguration.mapPort(port2, identifier1));

        // map ports that do not exist
        assertThrows(IllegalArgumentException.class, () -> portConfiguration.mapPort(PortConfiguration.NUM_PORTS, identifier1));
        assertThrows(IllegalArgumentException.class, () -> portConfiguration.mapPort(-2, identifier1));
    }

    @Test
    void unmapPort() {
        assertTrue(PortConfiguration.NUM_PORTS > 0, "There has to be at least one port enabled for the test to work");

        // map a port
        int port = 0;
        IIdentifier identifier = new Identifier(IdentifierType.IRI, "identifier");
        portConfiguration.mapPort(port, identifier);

        // test it has been mapped
        assertEquals(identifier, portConfiguration.getMappedIdentifier(port));

        // unmap and test
        portConfiguration.unmapPort(port);
        assertNull(portConfiguration.getMappedIdentifier(port));

        // unmap ports that do not exist
        assertThrows(IllegalArgumentException.class, () -> portConfiguration.unmapPort(PortConfiguration.NUM_PORTS));
        assertThrows(IllegalArgumentException.class, () -> portConfiguration.unmapPort(-2));
    }

    @Test
    void getMappedIdentifier() {
        assertTrue(PortConfiguration.NUM_PORTS > 0, "There has to be at least one port enabled for the test to work");
        int port = 0;

        // test unmapped port
        assertNull(portConfiguration.getMappedIdentifier(port));

        // map a port
        IIdentifier identifier = new Identifier(IdentifierType.IRI, "identifier");
        portConfiguration.mapPort(port, identifier);

        // test mapped port
        assertEquals(identifier, portConfiguration.getMappedIdentifier(port));

        // test ports that do not exist
        assertThrows(IllegalArgumentException.class, () -> portConfiguration.getMappedIdentifier(portConfiguration.NUM_PORTS));
        assertThrows(IllegalArgumentException.class, () -> portConfiguration.getMappedIdentifier(-1));
    }

    @Test
    void findPortForIdentifier() {
        assertTrue(PortConfiguration.NUM_PORTS > 1, "There have to be at least two port enabled for the test to work");

        // mapping a port
        int port1 = 0;
        IIdentifier mappedIdentifier1 = new Identifier(IdentifierType.IRI, "mappedIdentifier1");
        portConfiguration.mapPort(port1, mappedIdentifier1);

        // map another port
        int port2 = 1;
        IIdentifier mappedIdentifier2 = new Identifier(IdentifierType.IRI, "mappedIdentifier2");
        portConfiguration.mapPort(port2, mappedIdentifier2);

        assertEquals(port1, portConfiguration.findPortForIdentifier(mappedIdentifier1));
        assertEquals(port2, portConfiguration.findPortForIdentifier(mappedIdentifier2));
        Identifier notMappedIdentifier = new Identifier(IdentifierType.IRI, "notMappedIdentifier");
        assertNull(portConfiguration.findPortForIdentifier(notMappedIdentifier));
    }
}