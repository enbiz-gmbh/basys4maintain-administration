package de.enbiz.basyskgt.controller.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomepageUiController {

    @GetMapping("/ui")
    public String aasRegistration(Model model) {
        return "mainMenu";
    }

}
