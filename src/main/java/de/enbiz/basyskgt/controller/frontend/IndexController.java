package de.enbiz.basyskgt.controller.frontend;

import de.enbiz.basyskgt.configuration.BasyxInfrastructureConfig;
import de.enbiz.basyskgt.controller.RegistrationController;
import de.enbiz.basyskgt.controller.RegistrationStatusController;
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

    final RegistrationStatusController registrationStatusController;

    final BasyxInfrastructureConfig basyxInfrastructureConfig;

    @Autowired
    public IndexController(RegistrationController registrationController, RegistrationStatusController registrationStatusController, BasyxInfrastructureConfig basyxInfrastructureConfig) {
        this.registrationController = registrationController;
        this.registrationStatusController = registrationStatusController;
        this.basyxInfrastructureConfig = basyxInfrastructureConfig;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("registrationStatus", registrationStatusController.refreshAndGetAllRegistrationStatus());
        model.addAttribute("aasServerPath", basyxInfrastructureConfig.getAasServerPath());
        model.addAttribute("registryPath", basyxInfrastructureConfig.getRegistryServerPath());
        return "index";
    }
}
