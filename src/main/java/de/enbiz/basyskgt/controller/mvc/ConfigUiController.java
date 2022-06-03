package de.enbiz.basyskgt.controller.mvc;

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
        ConfigEntriesDTO configEntriesDTO = new ConfigEntriesDTO();
        for (ConfigParameter configParameter : ConfigParameter.values()) {
            configEntriesDTO.addConfigEntry(configRepository.getConfigEntry(configParameter));
        }
        model.addAttribute("configEntriesDTO", configEntriesDTO);
        return "configuration";
    }

    @PostMapping("/ui/config")
    public String serverConfigSubmit(@ModelAttribute ConfigEntriesDTO configEntriesDTO, Model model) {
        model.addAttribute("configEntries", configEntriesDTO);
        logger.debug("received ConfigEntriesDTO {}", configEntriesDTO);
        // TODO
        return "configuration";
    }
}