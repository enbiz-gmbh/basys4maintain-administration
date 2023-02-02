package de.enbiz.basyskgt.basyxInfrastructureConnection;

import de.enbiz.basyskgt.configuration.BasyxInfrastructureConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class BasyxInfrastructureStatusController {

    private final RestTemplate rest;
    private final Logger log = LoggerFactory.getLogger(BasyxInfrastructureStatusController.class);
    private final BasyxInfrastructureConfig infrastructureConfig;

    @Autowired
    public BasyxInfrastructureStatusController(BasyxInfrastructureConfig infrastructureConfig) {
        this.infrastructureConfig = infrastructureConfig;
        this.rest = new RestTemplate();
    }

    public boolean checkAasServerAccess() {
        log.debug("Checking aas server access");
        HttpStatus status = getUrlStatus(infrastructureConfig.getAasServerPath());
        if (status != null && status.is2xxSuccessful()) {
            log.info("aas server access successful");
            return true;
        } else {
            log.info("aas server access failed");
            return false;
        }
    }

    public boolean checkRegistryAccess() {
        log.debug("Checking registry access");
        HttpStatus status = getUrlStatus(infrastructureConfig.getRegistryServerPath());
        if (status != null && status.is2xxSuccessful()) {
            log.info("registry access successful");
            return true;
        } else {
            log.info("registry access failed");
            return false;
        }
    }

    /**
     * Accesses a given url and returns the {@link HttpStatus} of its response.
     *
     * @param url URL to check the status of
     * @return HttpStatus of the response. {@code null} if there was no response.
     */
    @Nullable
    private HttpStatus getUrlStatus(String url) {
        log.debug("requesting {}", url);
        HttpEntity<String> requestEntity = new HttpEntity<String>("", null);
        ResponseEntity<String> responseEntity;
        try {
            responseEntity = rest.exchange(url, HttpMethod.GET, requestEntity, String.class);
        } catch (Exception e) {
            log.info("connection to {} failed. Reason: {}", url, e.getMessage());
            return null;
        }
        log.debug("received status code {}", responseEntity.getStatusCode().value());
        return responseEntity.getStatusCode();
    }


}
