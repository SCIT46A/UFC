package app.scit46.ufc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/creator")
public class CreatorController {

    @GetMapping("/create")
    public String create() {
        return "creator/creator-create";
    }

    @GetMapping("/profile")
    public String profile() {
        return "creator/creator-profile";
    }

    @GetMapping("/campaign")
    public String campaign() {
        return "creator/creator-campaign";
    }

    @GetMapping("/delivery")
    public String delivery() {
        return "creator/creator-delivery";
    }
}
