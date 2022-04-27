package de.enbiz.basyskgt.persistence;

import de.enbiz.basyskgt.model.ConfigEntry;
import de.enbiz.basyskgt.model.ServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ConfigRepository {
    private final static String TABLE_CONFIG = "CONFIG";
    private final static String COLUMN_ID = "ID";
    private final static String COLUMN_VALUE = "VALUE";

    private final Logger log = LoggerFactory.getLogger(ConfigRepository.class);

    @Autowired
    JdbcTemplate jdbcTemplate;

    private Map<String, String> getConfigMap() {
        Map<String, String> result = new HashMap<>();
        String queryString = "SELECT " + COLUMN_ID + ", " + COLUMN_VALUE + " FROM " + TABLE_CONFIG;
        jdbcTemplate.query(queryString, (rs, rowNum) -> result.put(rs.getString(COLUMN_ID), rs.getString(COLUMN_VALUE)));
        log.info("Retrieved config map from DB");
        return result;
    }

    @Nullable
    public ConfigEntry getConfigParameter(ConfigParameter id) {
        String queryString = "SELECT " + COLUMN_VALUE + " FROM " + TABLE_CONFIG + " WHERE " + COLUMN_ID + "='" + id + "'";
        List<String> queryResult = jdbcTemplate.query(queryString, (rs, rowNum) -> rs.getString(COLUMN_VALUE));
        if (queryResult.size() == 0) {
            log.info("Config value for " + id + " is not set");
            return null;
        }
        String value = queryResult.iterator().next();
        log.info("Retrieved config mapping (" + id + ", " + value + ")");
        return new ConfigEntry(id, value);
    }

    public void setConfigParameter(ConfigParameter id, String value) {
        String queryString = "MERGE INTO " + TABLE_CONFIG + " VALUES('" + id + "', '" + value + "')";
        jdbcTemplate.execute(queryString);
    }

    public void setConfigParameter(ConfigEntry configEntry) {
        setConfigParameter(configEntry.getId(), configEntry.getValue());
    }

    public ServerConfig getServerConfig() {
        Map<String, String> config = getConfigMap();
        return new ServerConfig(config.get(ConfigParameter.REGISTRY_SERVER_PATH.name()), config.get(ConfigParameter.AAS_SERVER_PATH.name()));
    }

    public void setServerConfig(ServerConfig serverConfig) {
        setConfigParameter(ConfigParameter.AAS_SERVER_PATH, serverConfig.getAasServerPath());
        setConfigParameter(ConfigParameter.REGISTRY_SERVER_PATH, serverConfig.getRegistryPath());
    }

    @PostConstruct
    void init() {
        log.info("Initializing config Database...");

        //jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS " + TABLE_CONFIG + "(" + COLUMN_ID + " VARCHAR(255) PRIMARY KEY, " + COLUMN_VALUE + " VARCHAR(255))");

        if (!configTableExists()) {
            createAndPrefillConfigTable();
        }

        log.info("Config database initialized successfully");
    }

    public void resetConfig() {
        log.info("Dropping config table...");
        jdbcTemplate.execute("DROP TABLE " + TABLE_CONFIG + " IF EXISTS");
        createAndPrefillConfigTable();
        log.info("Config has been reset successfully");
    }

    /**
     * Checks if the table "CONFIG" already exists in the database
     *
     * @return true if a table with name "CONFIG" exists
     */
    private boolean configTableExists() {
        List<String> list = jdbcTemplate.queryForList("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES " +
                        "WHERE TABLE_TYPE='TABLE' " +
                        "AND TABLE_NAME='" + TABLE_CONFIG + "'",
                String.class);
        return list.contains(TABLE_CONFIG);
    }

    /**
     * Creates the configuration table and fills it with the default values.
     * Config table MUST NOT exist when calling this method.
     */
    private void createAndPrefillConfigTable() {
        jdbcTemplate.execute("CREATE TABLE " + TABLE_CONFIG + "(" + COLUMN_ID + " VARCHAR(255) PRIMARY KEY, " + COLUMN_VALUE + " VARCHAR(255))");
        for (ConfigParameter configParameter : ConfigParameter.values()) {
            jdbcTemplate.execute("INSERT INTO " + TABLE_CONFIG + " VALUES ('" + configParameter.name() + "', '" + configParameter.getDefaultValue() + "');");
        }
        log.info("Config table has been created and default values inserted");
    }

}
