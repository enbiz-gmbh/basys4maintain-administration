package de.enbiz.basyskgt.controller.mvc;

import de.enbiz.basyskgt.model.ConfigForm;
import de.enbiz.basyskgt.persistence.ConfigParameter;
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
public class ConfigUiController {

    Logger logger = LoggerFactory.getLogger(ConfigUiController.class);

    @Autowired
    ConfigRepository configRepository;


    @GetMapping("/ui/config")
    public String serverConfigForm(Model model) {
        logger.info("received GET /ui/config");
        ConfigForm configForm = new ConfigForm();
        for (ConfigParameter configParameter : ConfigParameter.values()) {
            configForm.addConfigEntry(configRepository.getConfigEntry(configParameter));
        }
        model.addAttribute("configForm", configForm);
        return "configuration";
    }

    @PostMapping("/ui/config")
    public String serverConfigSubmit(@ModelAttribute ConfigForm configForm, Model model) {
        logger.info("received POST /ui/config");
        logger.info("received ConfigEntriesDTO {}", configForm);
        model.addAttribute("configForm", configForm);
        configForm.getConfigEntries().forEach((key, value) -> configRepository.setConfigParameter(key, value));
        return "configuration";
    }
}