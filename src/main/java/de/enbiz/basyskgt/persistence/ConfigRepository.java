package de.enbiz.basyskgt.persistence;

import de.enbiz.basyskgt.model.ConfigParameter;
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
    public final static String AAS_SERVER_PATH = "aasServerPath";
    public final static String REGISTRY_SERVER_PATH = "registryServerPath";
    public final static String TABLE_CONFIG = "config";
    public final static String COLUMN_ID = "id";
    public final static String COLUMN_VALUE = "value";

    private final Logger log = LoggerFactory.getLogger(ConfigRepository.class);

    @Autowired
    JdbcTemplate jdbcTemplate;

    public Map<String, String> getConfigMap() {
        Map<String, String> result = new HashMap<>();
        String queryString = "SELECT " + COLUMN_ID + ", " + COLUMN_VALUE + " FROM " + TABLE_CONFIG;
        jdbcTemplate.query(queryString, (rs, rowNum) -> result.put(rs.getString(COLUMN_ID), rs.getString(COLUMN_VALUE)));
        log.info("Retrieved config map from DB");
        return result;
    }

    @Nullable
    public ConfigParameter getConfigParameter(String id) {
        String queryString = "SELECT " + COLUMN_VALUE + " FROM " + TABLE_CONFIG + " WHERE " + COLUMN_ID + "='" + id + "'";
        List<String> queryResult = jdbcTemplate.query(queryString, (rs, rowNum) -> rs.getString(COLUMN_VALUE));
        if (queryResult.size() == 0) {
            log.info("Config value for " + id + " is not set");
            return null;
        }
        String value = queryResult.iterator().next();
        log.info("Retrieved config mapping (" + id + ", " + value + ")");
        return new ConfigParameter(id, value);
    }

    private void setConfigParameter(String id, String value) {
        String queryString = "MERGE INTO " + TABLE_CONFIG + " VALUES('" + id + "', '" + value + "')";
        jdbcTemplate.execute(queryString);
    }

    public void setConfigParameter(ConfigParameter configParameter) {
        setConfigParameter(configParameter.getId(), configParameter.getValue());
    }

    public ServerConfig getServerConfig() {
        Map<String, String> config = getConfigMap();
        return new ServerConfig(config.get(ConfigRepository.REGISTRY_SERVER_PATH), config.get(ConfigRepository.AAS_SERVER_PATH));
    }

    public void setServerConfig(ServerConfig serverConfig) {
        setConfigParameter(AAS_SERVER_PATH, serverConfig.getAasServerPath());
        setConfigParameter(REGISTRY_SERVER_PATH, serverConfig.getRegistryPath());
    }

    @PostConstruct
    void init() {
        log.info("Creating tables...");

        jdbcTemplate.execute("DROP TABLE " + TABLE_CONFIG + " IF EXISTS");
        jdbcTemplate.execute("CREATE TABLE " + TABLE_CONFIG + "(" + COLUMN_ID + " VARCHAR(255) PRIMARY KEY, " + COLUMN_VALUE + " VARCHAR(255))");
    }

}
