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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@CrossOrigin
public class RegistrationRestController {

    private static final Logger log = LoggerFactory.getLogger(RegistrationRestController.class);

    @Autowired
    AASRegistryProxy aasRegistryProxy;

    @Autowired
    AASAggregatorProxy aasAggregatorProxy;

    @Autowired
    ConfigRepository configRepository;

    @Autowired
    AASBundle bsAasBundle;

    RegistrationStatus registrationStatus = RegistrationStatus.getInstance();

    @GetMapping("/api/registration/register")
    public ResponseEntity<String> register() {
        if (registrationStatus.isRegisteredToAasRegistry() && registrationStatus.isShellUploadedToRepository()) {
            return new ResponseEntity<>("AAS is already registered and uploaded to server", HttpStatus.CONFLICT);
        }
        if (!registrationStatus.isShellUploadedToRepository()) {
            log.info("Uploading AAS and submodels to AAS server");
            AASBundleHelper.integrate(aasAggregatorProxy, Collections.singleton(bsAasBundle));
            registrationStatus.setShellUploadedToRepository(true);
        }
        if (!registrationStatus.isRegisteredToAasRegistry()) {
            log.info("Registering AAS and submodels to registry");
            AASBundleHelper.register(aasRegistryProxy, Collections.singleton(bsAasBundle), configRepository.getServerConfig().getAasServerPath());
            registrationStatus.setRegisteredToAasRegistry(true);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/api/registration/deregister")
    public ResponseEntity<String> deregister() {
        if (!registrationStatus.isRegisteredToAasRegistry() && !registrationStatus.isShellUploadedToRepository()) {
            return new ResponseEntity<>("AAS is currently not registered", HttpStatus.CONFLICT);
        }
        if (registrationStatus.isRegisteredToAasRegistry()) {
            log.info("Deregistering AAS and submodels from registry");
            AASBundleHelper.deregister(aasRegistryProxy, Collections.singleton(bsAasBundle));
            registrationStatus.setRegisteredToAasRegistry(false);
        }
        if (registrationStatus.isShellUploadedToRepository()) {
            log.info("Deleting AAS and submodels from AAS server");
            aasAggregatorProxy.deleteAAS(bsAasBundle.getAAS().getIdentification());
            registrationStatus.setShellUploadedToRepository(false);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/api/registration/status")
    public ResponseEntity<RegistrationStatus> getStatus() {
        return new ResponseEntity<>(registrationStatus, HttpStatus.OK);
    }
}
