package de.enbiz.basyskgt.controller.rest;

import de.enbiz.basyskgt.model.RegistrationStatus;
import de.enbiz.basyskgt.persistence.ConfigRepository;
import org.eclipse.basyx.aas.aggregator.proxy.AASAggregatorProxy;
import org.eclipse.basyx.aas.registration.proxy.AASRegistryProxy;
import org.eclipse.basyx.support.bundle.AASBundle;
import org.eclipse.basyx.support.bundle.AASBundleHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
public class RegistrationRestController {

    private static Logger log = LoggerFactory.getLogger(RegistrationRestController.class);

    @Autowired
    RegistrationStatus registrationStatus;

    @Autowired
    AASRegistryProxy aasRegistryProxy;

    @Autowired
    AASAggregatorProxy aasAggregatorProxy;

    @Autowired
    ConfigRepository configRepository;

    @Autowired
    AASBundle bsAasBundle;

    @GetMapping("/api/registration/register")
    public ResponseEntity<Void> register() {
        // TODO only register if not yet registered
        log.info("Uploading AAS and submodels to AAS server");
        AASBundleHelper.integrate(aasAggregatorProxy, Collections.singleton(bsAasBundle));
        registrationStatus.setShellUploadedToRepository(true);
        log.info("Registering AAS and submodels to registry");
        AASBundleHelper.register(aasRegistryProxy, Collections.singleton(bsAasBundle), configRepository.getServerConfig().getAasServerPath());
        registrationStatus.setRegisteredToAasRegistry(true);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/api/registration/deregister")
    public ResponseEntity<Void> deregister() {
        // TODO only deregister if registered
        log.info("Deregistering AAS and submodels from registry");
        AASBundleHelper.deregister(aasRegistryProxy, Collections.singleton(bsAasBundle));
        registrationStatus.setRegisteredToAasRegistry(false);
        log.info("Deleting AAS and submodels from AAS server");
        aasAggregatorProxy.deleteAAS(bsAasBundle.getAAS().getIdentification());
        registrationStatus.setShellUploadedToRepository(false);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/api/registration/status")
    public ResponseEntity<RegistrationStatus> getStatus() {
        return new ResponseEntity<>(registrationStatus, HttpStatus.OK);
    }
}
