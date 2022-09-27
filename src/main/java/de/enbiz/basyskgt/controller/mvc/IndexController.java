package de.enbiz.basyskgt.controller.mvc;

import de.enbiz.basyskgt.basyx.LocalBasyxInfrastructureService;
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

    @Autowired
    LocalBasyxInfrastructureService localBasyxInfrastructureService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("registrationStatus", registrationStatus);
        model.addAttribute("localInfrastructureStatus", localBasyxInfrastructureService.getStatus());
        return "index";
    }
}
