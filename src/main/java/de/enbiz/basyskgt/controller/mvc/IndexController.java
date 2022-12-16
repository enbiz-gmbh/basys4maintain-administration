package de.enbiz.basyskgt.controller.mvc;

import de.enbiz.basyskgt.configuration.BasyxInfrastructureConfig;
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

    final RegistrationController registrationController;

    final BasyxInfrastructureConfig basyxInfrastructureConfig;

    @Autowired
    public IndexController(RegistrationController registrationController, BasyxInfrastructureConfig basyxInfrastructureConfig) {
        this.registrationController = registrationController;
        this.basyxInfrastructureConfig = basyxInfrastructureConfig;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("registrationStatus", registrationController.getStatus());
        model.addAttribute("aasServerPath", basyxInfrastructureConfig.getAasServerPath());
        model.addAttribute("registryPath", basyxInfrastructureConfig.getRegistryServerPath());
        return "index";
    }
}
