package de.enbiz.basyskgt.basyxInfrastructureConnection;

import de.enbiz.basyskgt.configuration.BasyxInfrastructureConfig;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Component
public class BasyxInfrastructureStatus {

    private final RestTemplate rest;
    private final Logger log = LoggerFactory.getLogger(BasyxInfrastructureStatus.class);
    private final BasyxInfrastructureConfig infrastructureConfig;
    @Getter
    private boolean aasServerAccess;
    @Getter
    private boolean registryAccess;

    @Autowired
    public BasyxInfrastructureStatus(BasyxInfrastructureConfig infrastructureConfig) {
        this.infrastructureConfig = infrastructureConfig;
        this.rest = new RestTemplate();
    }

    /**
     * When the AAS Server is unreachable, access should be rechecked every 10 seconds.
     */
    @Scheduled(fixedDelay = 10 * 1000)
    private void checkAasServerWhenNoAccess() throws IOException {
        if (aasServerAccess) {
            return;
        }
        log.debug("Checking aas server access");
        if (getUrlStatus(infrastructureConfig.getAasServerPath()).is2xxSuccessful()) {
            log.info("aas server access successful");
            this.aasServerAccess = true;
        } else {
            log.info("aas server access failed");
            this.aasServerAccess = false;
        }
    }

    /**
     * When the Registry Server is unreachable, access should be rechecked every 10 seconds.
     */
    @Scheduled(fixedDelay = 10 * 1000)
    private void checkRegistryWhenNoAccess() {
        if (registryAccess) {
            return;
        }
        log.debug("Checking registry access");
        if (getUrlStatus(infrastructureConfig.getRegistryServerPath()).is2xxSuccessful()) {
            log.info("registry access successful");
            this.registryAccess = true;
        } else {
            log.info("registry access failed");
            this.registryAccess = false;
        }
    }

    private HttpStatus getUrlStatus(String url) {
        log.debug("requesting {}", url);
        HttpEntity<String> requestEntity = new HttpEntity<String>("", null);
        ResponseEntity<String> responseEntity = rest.exchange(url, HttpMethod.GET, requestEntity, String.class);
        log.debug("received status code {}", responseEntity.getStatusCode().value());
        return responseEntity.getStatusCode();
    }


}
