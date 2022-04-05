package de.enbiz.basyskgt.controller;

import de.enbiz.basyskgt.model.ServerConfig;
import de.enbiz.basyskgt.persistence.ConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ServerConfigController {

    Logger logger = LoggerFactory.getLogger(ServerConfigController.class);

    @Autowired
    ConfigRepository configRepository;


    @GetMapping("/serverConfig")
    public String serverConfigForm(Model model) {
        model.addAttribute("serverConfig", configRepository.getServerConfig());
        return "serverConfig";
    }

    @PostMapping("/serverConfig")
    public String serverConfigSubmit(@ModelAttribute ServerConfig serverConfig, Model model) {
        model.addAttribute("serverConfig", serverConfig);
        configRepository.setServerConfig(serverConfig);
        return "serverConfig";
    }
}