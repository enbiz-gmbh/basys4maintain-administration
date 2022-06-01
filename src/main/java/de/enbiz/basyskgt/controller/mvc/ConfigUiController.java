package de.enbiz.basyskgt.controller.mvc;

import de.enbiz.basyskgt.controller.rest.ConfigRestController;
import de.enbiz.basyskgt.model.ServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ConfigUiController {

    Logger logger = LoggerFactory.getLogger(ConfigUiController.class);

    @Autowired
    ConfigRestController configRestController;


    @GetMapping("/ui/config")
    public String serverConfigForm(Model model) {
        model.addAttribute("serverConfig", configRestController.getServerConfig());
        return "configuration";
    }

    @PostMapping("/ui/config")
    public String serverConfigSubmit(@ModelAttribute ServerConfig serverConfig, Model model) {
        model.addAttribute("serverConfig", serverConfig);
        configRestController.setServerConfig(serverConfig);
        return "configuration";
    }
}