package de.enbiz.basyskgt.persistence;

import de.enbiz.basyskgt.model.ConfigEntry;
import de.enbiz.basyskgt.model.ServerConfig;
import net.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.util.Arrays;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ConfigRepositoryTest {

    private static Logger log = LoggerFactory.getLogger(ConfigRepositoryTest.class);
    @Autowired
    ConfigRepository configRepository;

    @BeforeAll
    static void setup() {
        // delete database file before executing tests so a new DB is created at each test run
        File dbFile = new File("data/dbTest.mv.db");
        dbFile.delete();
    }

    /**
     * Tests correct initialization of the DB. Checks that all ConfigParameters have their default value assigned after DB creation.
     */
    @Test
    @Order(0)
    void dbInitializationTest() {
        assertAllConfigValuesSetToDefault();
    }

    @Test
    void configSetAndResetTest() {
        String value = RandomString.make(20);
        for (ConfigParameter configParameter : ConfigParameter.values()) {
            configRepository.setConfigParameter(configParameter, value);
        }
        assertAllConfigValuesSetToValue(value);
        configRepository.resetConfig();
        assertAllConfigValuesSetToDefault();
    }

    @Test
    void setConfigEntryTest() {
        ConfigParameter parameter = Arrays.stream(ConfigParameter.values()).iterator().next();
        String value = RandomString.make(20);
        ConfigEntry entry = new ConfigEntry(parameter, value);
        configRepository.setConfigParameter(entry);
        Assertions.assertEquals(configRepository.getConfigEntry(parameter), entry);
    }

    @Test
    void setAndGetServerConfigTest() {
        ServerConfig serverConfig = new ServerConfig(RandomString.make(20), RandomString.make(20));
        configRepository.setServerConfig(serverConfig);
        Assertions.assertEquals(serverConfig, configRepository.getServerConfig());
    }

    private void assertAllConfigValuesSetToDefault() {
        for (ConfigParameter configParameter : ConfigParameter.values()) {
            ConfigEntry entry = configRepository.getConfigEntry(configParameter);
            Assertions.assertNotNull(entry);
            Assertions.assertEquals(configParameter.getDefaultValue(), entry.getValue());
        }
    }

    private void assertAllConfigValuesSetToValue(String value) {
        for (ConfigParameter configParameter : ConfigParameter.values()) {
            ConfigEntry entry = configRepository.getConfigEntry(configParameter);
            Assertions.assertNotNull(entry);
            Assertions.assertEquals(value, entry.getValue());
        }
    }
}
