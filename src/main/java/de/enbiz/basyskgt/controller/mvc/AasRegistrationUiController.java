package de.enbiz.basyskgt.controller.mvc;

import de.enbiz.basyskgt.model.RegistrationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AasRegistrationUiController {

    private final Logger log = LoggerFactory.getLogger(AasRegistrationUiController.class);

    RegistrationStatus registrationStatus = RegistrationStatus.getInstance();

    @GetMapping("/ui/aas-registration")
    public String aasRegistration(Model model) {
        model.addAttribute("registrationStatus", registrationStatus);
        return "aas-registration";
    }

    @PostMapping("/ui/aas-registration")
    public String registerAas(Model model) {
        log.info("Pushing AAS to repository Server");
        // TODO upload AAS
        log.info("Registering AAS at registry");
        // TODO register AAS
        return "aas-registration";
    }
}
