package de.enbiz.basyskgt.controller.mvc;

import de.enbiz.basyskgt.controller.LocalBasyxInfrastructureController;
import de.enbiz.basyskgt.model.RegistrationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    private final Logger log = LoggerFactory.getLogger(IndexController.class);

    RegistrationStatus registrationStatus = RegistrationStatus.getInstance();

    final LocalBasyxInfrastructureController localBasyxInfrastructureController;

    @Autowired
    public IndexController(LocalBasyxInfrastructureController localBasyxInfrastructureController) {
        this.localBasyxInfrastructureController = localBasyxInfrastructureController;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("registrationStatus", registrationStatus);
        model.addAttribute("localInfrastructureStatus", localBasyxInfrastructureController.getStatus());
        return "index";
    }
}
