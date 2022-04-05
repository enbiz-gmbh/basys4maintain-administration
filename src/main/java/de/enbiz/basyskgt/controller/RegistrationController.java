package de.enbiz.basyskgt.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegistrationController {

    Logger log = LoggerFactory.getLogger(RegistrationController.class);

    @GetMapping("/aasRegistration")
    public String aasRegistration(Model model) {
        return "aasRegistration";
    }

    @PostMapping("/registerAas")
    public String registerAas(Model model) {
        log.info("Pushing AAS to repository Server");
        // TODO upload AAS
        log.info("Registering AAS at registry");
        // TODO register AAS
        return "aasRegistration";
    }
}
