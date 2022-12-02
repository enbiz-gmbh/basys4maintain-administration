package de.enbiz.basyskgt.controller.mvc;

import de.enbiz.basyskgt.controller.LocalBasyxInfrastructureController;
import de.enbiz.basyskgt.controller.RegistrationController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    private final Logger log = LoggerFactory.getLogger(IndexController.class);

    final LocalBasyxInfrastructureController localBasyxInfrastructureController;

    final RegistrationController registrationController;

    @Autowired
    public IndexController(LocalBasyxInfrastructureController localBasyxInfrastructureController, RegistrationController registrationController) {
        this.localBasyxInfrastructureController = localBasyxInfrastructureController;
        this.registrationController = registrationController;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("registrationStatus", registrationController.getStatus());
        model.addAttribute("localInfrastructureStatus", localBasyxInfrastructureController.getStatus());
        return "index";
    }
}
